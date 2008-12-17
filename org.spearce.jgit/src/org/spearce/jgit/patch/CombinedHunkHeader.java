/*
 * Copyright (C) 2008, Google Inc.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the following
 * conditions are met:
 *
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above
 *   copyright notice, this list of conditions and the following
 *   disclaimer in the documentation and/or other materials provided
 *   with the distribution.
 *
 * - Neither the name of the Git Development Community nor the
 *   names of its contributors may be used to endorse or promote
 *   products derived from this software without specific prior
 *   written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.spearce.jgit.patch;

import static org.spearce.jgit.util.RawParseUtils.nextLF;
import static org.spearce.jgit.util.RawParseUtils.parseBase10;

import org.spearce.jgit.lib.AbbreviatedObjectId;
import org.spearce.jgit.util.MutableInteger;

/** Hunk header for a hunk appearing in a "diff --cc" style patch. */
public class CombinedHunkHeader extends HunkHeader {
	private static abstract class CombinedOldImage extends OldImage {
		int nContext;
	}

	private CombinedOldImage[] old;

	CombinedHunkHeader(final CombinedFileHeader fh, final int offset) {
		super(fh, offset, null);
		old = new CombinedOldImage[fh.getParentCount()];
		for (int i = 0; i < old.length; i++) {
			final int imagePos = i;
			old[i] = new CombinedOldImage() {
				@Override
				public AbbreviatedObjectId getId() {
					return fh.getOldId(imagePos);
				}
			};
		}
	}

	@Override
	public CombinedFileHeader getFileHeader() {
		return (CombinedFileHeader) super.getFileHeader();
	}

	@Override
	public OldImage getOldImage() {
		return getOldImage(0);
	}

	/**
	 * Get the OldImage data related to the nth ancestor
	 *
	 * @param nthParent
	 *            the ancestor to get the old image data of
	 * @return image data of the requested ancestor.
	 */
	public OldImage getOldImage(final int nthParent) {
		return old[nthParent];
	}

	@Override
	void parseHeader(final int end) {
		// Parse "@@@ -55,12 -163,13 +163,15 @@@ protected boolean"
		//
		final byte[] buf = file.buf;
		final MutableInteger ptr = new MutableInteger();
		ptr.value = nextLF(buf, startOffset, ' ');

		for (int n = 0; n < old.length; n++) {
			old[n].startLine = -parseBase10(buf, ptr.value, ptr);
			if (buf[ptr.value] == ',')
				old[n].lineCount = parseBase10(buf, ptr.value + 1, ptr);
			else
				old[n].lineCount = 1;
		}

		newStartLine = parseBase10(buf, ptr.value + 1, ptr);
		if (buf[ptr.value] == ',')
			newLineCount = parseBase10(buf, ptr.value + 1, ptr);
		else
			newLineCount = 1;
	}

	@Override
	int parseBody(final Patch script, final int end) {
		final byte[] buf = file.buf;
		int c = nextLF(buf, startOffset);

		for (final CombinedOldImage o : old) {
			o.nDeleted = 0;
			o.nAdded = 0;
			o.nContext = 0;
		}
		nContext = 0;
		int nAdded = 0;

		SCAN: for (int eol; c < end; c = eol) {
			eol = nextLF(buf, c);

			if (eol - c < old.length + 1) {
				// Line isn't long enough to mention the state of each
				// ancestor. It must be the end of the hunk.
				break SCAN;
			}

			switch (buf[c]) {
			case ' ':
			case '-':
			case '+':
				break;

			default:
				// Line can't possibly be part of this hunk; the first
				// ancestor information isn't recognizable.
				//
				break SCAN;
			}

			int localcontext = 0;
			for (int ancestor = 0; ancestor < old.length; ancestor++) {
				switch (buf[c + ancestor]) {
				case ' ':
					localcontext++;
					old[ancestor].nContext++;
					continue;

				case '-':
					old[ancestor].nDeleted++;
					continue;

				case '+':
					old[ancestor].nAdded++;
					nAdded++;
					continue;

				default:
					break SCAN;
				}
			}
			if (localcontext == old.length)
				nContext++;
		}

		for (int ancestor = 0; ancestor < old.length; ancestor++) {
			final CombinedOldImage o = old[ancestor];
			final int cmp = o.nContext + o.nDeleted;
			if (cmp < o.lineCount) {
				final int missingCnt = o.lineCount - cmp;
				script.error(buf, startOffset, "Truncated hunk, at least "
						+ missingCnt + " lines is missing for ancestor "
						+ (ancestor + 1));
			}
		}

		if (nContext + nAdded < newLineCount) {
			final int missingCount = newLineCount - (nContext + nAdded);
			script.error(buf, startOffset, "Truncated hunk, at least "
					+ missingCount + " new lines is missing");
		}

		return c;
	}
}