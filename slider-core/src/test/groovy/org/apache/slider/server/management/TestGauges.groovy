/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.slider.server.management

import org.apache.slider.server.appmaster.management.LongGauge
import org.apache.slider.test.SliderTestBase
import org.junit.Test

class TestGauges extends  SliderTestBase {

  @Test
  public void testLongGaugeOperations() throws Throwable {
    LongGauge gauge = new LongGauge();
    assert gauge.get() == 0
    gauge.inc()
    assert gauge.get() == 1
    gauge.inc()
    assert gauge.get() == 2
    gauge.inc()
    assert gauge.get() == 3
    assert gauge.getValue() == gauge.get()
    assert gauge.count == gauge.get()

    gauge.dec()
    assert gauge.get() == 2
    assert gauge.decToFloor(1) == 1
    assert gauge.get() == 1
    assert gauge.decToFloor(1) == 0
    assert gauge.decToFloor(1) == 0
    assert gauge.decToFloor(0) == 0

    gauge.set(4)
    assert gauge.decToFloor(8) == 0

  }
}
