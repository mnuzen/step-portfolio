// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import io.pkts.PacketHandler;
import io.pkts.Pcap;
import io.pkts.buffer.Buffer;
import io.pkts.packet.Packet;
import io.pkts.packet.TCPPacket;
import io.pkts.packet.UDPPacket;
import io.pkts.protocol.Protocol;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.*; 
import com.google.gson.Gson;

import java.io.IOException;
import java.io.*;


/** Servlet that processes comments.*/
@WebServlet("/PCAP-data")
public class PacketParserServlet extends HttpServlet {
  //static final String FILENAME = "portfolio/src/main/webapp/WEB-INF/gmail.pcap";
  private ArrayList<String> packets = new ArrayList<String>();

  public void main() {
    try {
        final InputStream stream = new FileInputStream("WEB-INF/traffic.pcap");
        final Pcap pcap = Pcap.openStream(stream);

        pcap.loop(new PacketHandler() {
        @Override
        public boolean nextPacket(final Packet packet) throws IOException {
          if (packet.hasProtocol(Protocol.TCP)) {
            TCPPacket tcpPacket = (TCPPacket) packet.getPacket(Protocol.TCP);
            Buffer buffer = tcpPacket.getPayload();
            if (buffer != null) {
              String text = "TCP Destination IP Address: " + tcpPacket.getDestinationIP() + "\n";
              System.out.println(text);
              packets.add(text);
            }
          } 
            
          else if (packet.hasProtocol(Protocol.UDP)) {
            UDPPacket udpPacket = (UDPPacket) packet.getPacket(Protocol.UDP);
            Buffer buffer = udpPacket.getPayload();
            if (buffer != null) {
              String text = "UDP Destination IP Address: " + udpPacket.getDestinationIP() + "\n"; 
              packets.add(text);
            }
          }
        return true;
        }
      });
      pcap.close();
    }
    catch(FileNotFoundException ex) {
        packets.add("file not found");
    }
    catch(IOException ex) {
        packets.add("io err");
    }
    // final Pcap pcap = Pcap.openStream("/portfolio/src/main/webapp/WEB-INF/nuzen_fitbit_data.csv");
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException { 
    main(); 
    response.setContentType("application/json;");

    // Convert the ArrayList to JSON
    String json = convertToJsonUsingGson(packets);

    // Send the JSON as the response
    response.getWriter().println(json);
  }

  /**
   * Converts a DataServlet instance into a JSON string using the Gson library.
   */
  private String convertToJsonUsingGson(ArrayList<String> data) {
    Gson gson = new Gson();
    String json = gson.toJson(data);
    return json;
  }
}