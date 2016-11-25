/*
 * Copyright (C) 2016 Kailash Dabhi (Kingbull Technology)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package vendor.omrecorder;

import java.io.IOException;
import java.io.OutputStream;

/**
 * An Implementer class should use this interface to write encoded
 * audio chunk to OutputStream according to chosen audio format.
 *
 * @author Kailash Dabhi (kailash09dabhi@gmail.com)
 * @date 06-07-2016
 * @skype kailash.09
 */
public interface WriteAction {

  /**
   * Implement this behaviour to provide custom Write Action for audio which
   * requires {@code data} to encode. So here One can encode the data
   * according to chosen audio format.
   */
  void execute(byte[] data, OutputStream outputStream) throws IOException;

  /**
   * Use this default implementation to write data directly without any encoding to OutputStream.
   */
  final class Default implements WriteAction {

    @Override public void execute(byte[] data, OutputStream outputStream) throws IOException {
      outputStream.write(data);
    }
  }
}

