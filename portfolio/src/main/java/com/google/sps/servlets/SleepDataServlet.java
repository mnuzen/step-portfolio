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

import com.google.gson.Gson;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import java.util.Scanner;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@WebServlet("/sleep-data")
public class SleepDataServlet extends HttpServlet {
  static final String FILENAME = "/WEB-INF/nuzen_fitbit_data.csv";
  static final int MINIMUM_STRING_LENGTH = 5;

  static final int TIMESTAMP_INDEX = 0;
  static final int STRING_START_INDEX = 1;
  static final int DATE_INDEX = 11;
  static final int ASLEEP_INDEX = 2;
  static final String NULL_TIME = "0";

  private Map<String, Double> sleepData = new HashMap<>();
  
  /** Read in Fitbit CSV file and put (string_date, time_asleep) into HashMap. */
  @Override
  public void init() {
    Scanner scanner = new Scanner(getServletContext().getResourceAsStream(FILENAME));
    while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        if (line.length() > MINIMUM_STRING_LENGTH) {    
          String[] rawCellData = line.split(",");

          /** Parse Fitbit data, which comes in the following format:
              "2020-05-26 2:29AM","2020-05-26 9:58AM","362","87","22","449","47","247","68" */
          String[] cellData = parseFitbitData(rawCellData);

          // retrieve date data
          String date = cellData[0];
          // retrieve time asleep data
          try {
            Double timeAsleep = (double)Integer.parseInt(cellData[1]);
            // store both data into hashmap
            sleepData.put(date, timeAsleep);
          }
          catch(NumberFormatException ex) {
            System.err.println("Invalid string in argument for time asleep");  
          }
        }
    }
    scanner.close();
  }

  /** Generate JSON return with sleepData. */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    Gson gson = new Gson();
    String json = gson.toJson(sleepData);
    response.getWriter().println(json);
  }

  /** Parse Fitbit data to retrieve date and minutes asleep.
      * @param cellData containing one row of Fitbit data
      * @return return_data containing date in String format at index[0] and time asleep in minutes in String format at index[1]. */
  private String[] parseFitbitData(String[] cellData) {
    // parse date into string format
    String timestamp = cellData[TIMESTAMP_INDEX];
    String date = timestamp.substring(STRING_START_INDEX, DATE_INDEX);
    
    // parse sleep minutes into string format
    String asleepStamp = cellData[ASLEEP_INDEX];
    String timeAsleepMinutes = NULL_TIME;

    try {
        timeAsleepMinutes = asleepStamp.substring(STRING_START_INDEX, asleepStamp.length()-1);
    }
    catch(StringIndexOutOfBoundsException ex) {
      System.err.println(ex.getMessage());
    }
    
    // package and return date and sleep minutes
    String[] returnData = new String[2];
    returnData[0] = date;
    returnData[1] = timeAsleepMinutes; 

    return returnData;
  }
}

  