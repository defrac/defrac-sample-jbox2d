JBo2D Benchmark
===============
This sample is a port of the [jbox2d-benchmark](http://teavm.org/live-examples/jbox2d-benchmark/).

Note that the JBox2D code has been modified to allow this benchmark to run. The original benchmark
code triggers an assertion error in JBox2D that is not reached when compiling with GWT or TeaVM.
Therefore the JBox2D code was modified to allow it to run with a broken shape.

Online Demo
===========
http://www.defrac.com/sample-jbox2d/
