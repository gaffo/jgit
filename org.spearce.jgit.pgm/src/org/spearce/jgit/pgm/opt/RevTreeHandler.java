/*
 * Copyright (C) 2008, Shawn O. Pearce <spearce@spearce.org>
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

package org.spearce.jgit.pgm.opt;

import java.io.IOException;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.OptionHandler;
import org.kohsuke.args4j.spi.Parameters;
import org.kohsuke.args4j.spi.Setter;
import org.spearce.jgit.errors.IncorrectObjectTypeException;
import org.spearce.jgit.errors.MissingObjectException;
import org.spearce.jgit.lib.ObjectId;
import org.spearce.jgit.revwalk.RevTree;

/**
 * Custom argument handler {@link RevTree} from string values.
 * <p>
 * Assumes the parser has been initialized with a Repository.
 */
public class RevTreeHandler extends OptionHandler<RevTree> {
	private final org.spearce.jgit.pgm.opt.CmdLineParser clp;

	/**
	 * Create a new handler for the command name.
	 * <p>
	 * This constructor is used only by args4j.
	 *
	 * @param parser
	 * @param option
	 * @param setter
	 */
	public RevTreeHandler(final CmdLineParser parser, final OptionDef option,
			final Setter<? super RevTree> setter) {
		super(parser, option, setter);
		clp = (org.spearce.jgit.pgm.opt.CmdLineParser) parser;
	}

	@Override
	public int parseArguments(final Parameters params) throws CmdLineException {
		final String name = params.getParameter(0);
		final ObjectId id;
		try {
			id = clp.getRepository().resolve(name);
		} catch (IOException e) {
			throw new CmdLineException(e.getMessage());
		}
		if (id == null)
			throw new CmdLineException(name + " is not a tree");

		final RevTree c;
		try {
			c = clp.getRevWalk().parseTree(id);
		} catch (MissingObjectException e) {
			throw new CmdLineException(name + " is not a tree");
		} catch (IncorrectObjectTypeException e) {
			throw new CmdLineException(name + " is not a tree");
		} catch (IOException e) {
			throw new CmdLineException("cannot read " + name + ": "
					+ e.getMessage());
		}
		setter.addValue(c);
		return 1;
	}

	@Override
	public String getDefaultMetaVariable() {
		return "tree-ish";
	}
}
