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
  static final int DATE_SIZE = 10;
  private Map<String, Double> sleepData = new HashMap<>();
  
  /** Read in Fitbit CSV file and put (string_date, time_asleep) into HashMap. */
  @Override
  public void init() {
    Scanner scanner = new Scanner(getServletContext().getResourceAsStream(FILENAME));
    while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        String[] cell_data = line.split(",");

        /** Parse time, which comes in the following format:
            "2020-05-26 2:29AM","2020-05-26 9:58AM","362","87","22","449","47","247","68" */
        String timestamp = cell_data[0];
        String string_date = timestamp.substring(1, DATE_SIZE+1);
        Double time_asleep = (double)Integer.parseInt(cell_data[2].substring(1, cell_data[2].length()-1));
        sleepData.put(string_date, time_asleep);
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
}

  