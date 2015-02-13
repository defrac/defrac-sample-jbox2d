package org.teavm.samples.benchmark.defrac;

import defrac.dni.Intrinsic;

/**
 *
 */
@Intrinsic("Performance")
public final class Performance {
  @Intrinsic
  public native double now();
}
