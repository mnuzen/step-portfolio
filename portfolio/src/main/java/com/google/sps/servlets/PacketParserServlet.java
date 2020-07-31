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

import com.google.sps.datastore.PCAPdata;
import com.google.sps.datastore.GenericPCAPDao;

import io.pkts.PacketHandler;
import io.pkts.Pcap;
import io.pkts.buffer.Buffer;
import io.pkts.packet.Packet;
import io.pkts.packet.TCPPacket;
import io.pkts.packet.UDPPacket;
import io.pkts.packet.IPPacket;
import io.pkts.protocol.Protocol;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;

import java.util.*; 
import com.google.gson.Gson;

import java.io.IOException;
import java.io.*;


/** Servlet that processes comments.*/
@WebServlet("/PCAP-data")
public class PacketParserServlet extends HttpServlet {
  ArrayList<PCAPdata> mockData = new ArrayList<PCAPdata>();
  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService(); //creates database

  private ArrayList<String> packets = new ArrayList<String>();

  //static final String FILENAME = "WEB-INF/files/traffic.pcap";
  static final String FILENAME = "WEB-INF/files/chargen-tcp.pcap";
  //static final String FILENAME = "WEB-INF/chargen-udp.pcap";

  String protocol = "";
  String ports = "";
  String srcip = "";
  String dstip = "";

  public void main() {
    try {
        final InputStream stream = new FileInputStream(FILENAME);
        final Pcap pcap = Pcap.openStream(stream);

        pcap.loop(new PacketHandler() {
        @Override
        public boolean nextPacket(final Packet packet) throws IOException {
          if(packet.hasProtocol(Protocol.IPv4)) {
            // initialize new datastore entry

            IPPacket ip = (IPPacket) packet.getPacket(Protocol.IPv4);
            protocol = "IPv4";
            ports = "";
            
            //The IP addresses involved
            String dstip = ip.getDestinationIP();
            String srcip = ip.getSourceIP();
            // The payload data as hex
            String payload = ip.getPayload().dumpAsHex();
            // Time packet arrived.
            long packetTime = ip.getArrivalTime(); 
            // Is this packet a fragment?
            boolean isFragment = ip.isFragmented();

            if (packet.hasProtocol(Protocol.UDP)) {
              protocol = "UDP";
              UDPPacket udpPacket = (UDPPacket) packet.getPacket(Protocol.UDP);
              int dstport = udpPacket.getDestinationPort();
              int srcport = udpPacket.getSourcePort();
              ports = "Destination Port: " + dstport + " Source Port: " + srcport;
            }
            else if (packet.hasProtocol(Protocol.TCP)) {
              protocol = "TCP";
              TCPPacket tcpPacket = (TCPPacket) packet.getPacket(Protocol.TCP);
              int dstport = tcpPacket.getDestinationPort();
              int srcport = tcpPacket.getSourcePort();
              ports = "Destination Port: " + dstport + " Source Port: " + srcport;
            }

            String text = protocol + " Packet from " + dstip + " to " + srcip + " at time " + packetTime;
            text += "; " + ports + "\n";
            packets.add(text);
            
        }
        return true;
        }
      });
      pcap.close();
    }
    catch(FileNotFoundException ex) {
        System.out.println("File not found");
    }
    catch(IOException ex) {
        System.out.println("IO err");
    }
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

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      final InputStream stream = new FileInputStream(FILENAME);
      final Pcap pcap = Pcap.openStream(stream);

      pcap.loop(new PacketHandler() {
      @Override
      public boolean nextPacket(final Packet packet) throws IOException {
        if(packet.hasProtocol(Protocol.IPv4)) {
          // initialize new datastore entry

          IPPacket ip = (IPPacket) packet.getPacket(Protocol.IPv4);
          protocol = "IPv4";
          ports = "";
          
          //The IP addresses involved
          String dstip = ip.getDestinationIP();
          String srcip = ip.getSourceIP();
          // The payload data as hex
          String payload = ip.getPayload().dumpAsHex();
          // Time packet arrived.
          long packetTime = ip.getArrivalTime(); 
          // Is this packet a fragment?
          boolean isFragment = ip.isFragmented();

          if (packet.hasProtocol(Protocol.UDP)) {
            protocol = "UDP";
            UDPPacket udpPacket = (UDPPacket) packet.getPacket(Protocol.UDP);
            int dstport = udpPacket.getDestinationPort();
            int srcport = udpPacket.getSourcePort();
            ports = "Destination Port: " + dstport + " Source Port: " + srcport;
          }
          else if (packet.hasProtocol(Protocol.TCP)) {
            protocol = "TCP";
            TCPPacket tcpPacket = (TCPPacket) packet.getPacket(Protocol.TCP);
            int dstport = tcpPacket.getDestinationPort();
            int srcport = tcpPacket.getSourcePort();
            ports = "Destination Port: " + dstport + " Source Port: " + srcport;
          }

          String text = protocol + " Packet from " + dstip + " to " + srcip + " at time " + packetTime;
          text += "; " + ports + "\n";
          packets.add(text);
          Random r = new Random();
          int freq = r.nextInt(14)+1;

          // PCAPdata tempPCAP = new PCAPdata(source, destination, domain, location, protocol, size, flagged, frequency);
          PCAPdata tempPCAP = new PCAPdata(srcip, dstip, "wiki", "loc", protocol, 2, false, freq);

          GenericPCAPDao data = new GenericPCAPDao();
          data.setPCAPObjects(tempPCAP, "file_1");
          
        }
        return true;
      }
      });
    pcap.close();
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