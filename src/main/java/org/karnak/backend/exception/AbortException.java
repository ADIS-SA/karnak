/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.exception;

import org.weasis.dicom.param.AttributeEditorContext.Abort;

public final class AbortException extends IllegalStateException {

  private static final long serialVersionUID = 3993065212756372490L;
  private final Abort abort;

  public AbortException(Abort abort, String s) {
    super(s);
    this.abort = abort;
  }

  public AbortException(Abort abort, String string, Exception e) {
    super(string, e);
    this.abort = abort;
  }

  @Override
  public String toString() {
    return getMessage();
  }

  public Abort getAbort() {
    return abort;
  }
}