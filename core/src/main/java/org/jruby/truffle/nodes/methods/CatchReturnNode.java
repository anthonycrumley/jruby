/*
 * Copyright (c) 2013, 2014 Oracle and/or its affiliates. All rights reserved. This
 * code is released under a tri EPL/GPL/LGPL license. You can use it,
 * redistribute it and/or modify it under the terms of the:
 *
 * Eclipse Public License version 1.0
 * GNU General Public License version 2
 * GNU Lesser General Public License version 2.1
 */
package org.jruby.truffle.nodes.methods;

import com.oracle.truffle.api.*;
import com.oracle.truffle.api.source.*;
import com.oracle.truffle.api.frame.*;
import com.oracle.truffle.api.utilities.*;
import com.oracle.truffle.api.CompilerDirectives.*;
import org.jruby.truffle.nodes.*;
import org.jruby.truffle.runtime.*;
import org.jruby.truffle.runtime.control.*;

/**
 * Catch a {@code return} jump at the root of a method.
 */
public class CatchReturnNode extends RubyNode {

    @Child protected RubyNode body;
    private final long returnID;

    private final BranchProfile returnProfile = BranchProfile.create();
    private final BranchProfile returnToOtherMethodProfile = BranchProfile.create();

    public CatchReturnNode(RubyContext context, SourceSection sourceSection, RubyNode body, long returnID) {
        super(context, sourceSection);
        this.body = body;
        this.returnID = returnID;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        try {
            return body.execute(frame);
        } catch (ReturnException e) {
            returnProfile.enter();

            if (e.getReturnID() == returnID) {
                return e.getValue();
            } else {
                returnToOtherMethodProfile.enter();
                throw e;
            }
        }
    }

}
