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

/** Servlet that processes comments.*/
@WebServlet("/PCAP-data")
public class PacketParserServlet extends HttpServlet {
  static final String FILENAME = "/WEB-INF/gmail.pcap";
  private ArrayList<String> packets = new ArrayList<String>();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {  
    final Pcap pcap = Pcap.openStream("/WEB-INF/gmail.pcap");

    pcap.loop(new PacketHandler() {
        @Override
        public boolean nextPacket(Packet packet) throws IOException {
          if (packet.hasProtocol(Protocol.TCP)) {
            TCPPacket tcpPacket = (TCPPacket) packet.getPacket(Protocol.TCP);
            Buffer buffer = tcpPacket.getPayload();
            //if (buffer != null) {
              String text = "TCP: " + buffer;
              System.out.println(text);
              packets.add(text);
            //}

          } 
            
          else if (packet.hasProtocol(Protocol.UDP)) {
            UDPPacket udpPacket = (UDPPacket) packet.getPacket(Protocol.UDP);
            Buffer buffer = udpPacket.getPayload();
            //if (buffer != null) {
              String text = "UDP: " + buffer;
              packets.add(text);
            //}
          }
          
          return true;
        }
    });

    // Convert the ArrayList to JSON
    String json = convertToJsonUsingGson(packets);

    // Send the JSON as the response
    response.setContentType("application/json;");
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