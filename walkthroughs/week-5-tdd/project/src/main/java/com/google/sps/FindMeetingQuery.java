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

package com.google.sps;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import java.lang.*;

public final class FindMeetingQuery {
  /** MeetingRequest request has:
      getAttendees() method which returns hashSet of mandatory attendees
      getDuration() method which tells us how long our meeting has to be */

  /** Collection<Event> events has events. Each event has:
      getWhen() method which tells us time range of event
      getAttendees() method which tells us who has to go to each event */

  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    System.out.println("\n");
    Collection<String> mandatoryAttendees = request.getAttendees();
    System.out.println("Mandatory Attendees: " + mandatoryAttendees);

    // Check edge cases
    MeetingRequest requestAllMandatory = new MeetingRequest (mandatoryAttendees, request.getDuration());
    ArrayList<TimeRange> solutions = new ArrayList<>();

    // Requests with duration longer than a day, return no solutions. 
    if (requestAllMandatory.getDuration() > TimeRange.WHOLE_DAY.duration()){
      return solutions;
    }

    // If there are no attendees or meeting duration is 0, return whole day as solution. 
    if (requestAllMandatory.getAttendees().isEmpty() || requestAllMandatory.getDuration() == 0){
      solutions.add(TimeRange.WHOLE_DAY);
      return solutions;
    }

    ArrayList<TimeRange> relevantTimes = new ArrayList<>();

    // Iterate through all events, ensuring each event has at least one member from mandatoryAttendees    
    Iterator<Event> iter = events.iterator();
    while (iter.hasNext()) {
      Event event = iter.next();
      Set<String> attendees = event.getAttendees();
      boolean overlap = false;

      for (String attendee : attendees) {
        if (mandatoryAttendees.contains(attendee)){
          overlap = true; // overlap is true if this event has a mandatory attendee
          System.out.print("Overlap Attendee: " + attendee + "; ");
          break;
        }
      }

      if (overlap) {
        relevantTimes.add(event.getWhen());
        System.out.println("Added " + event.getTitle() + " at time " + event.getWhen());
      }
    }

    // Condense overlapping ranges in events (assuming events are sorted)
    ArrayList<TimeRange> consolidatedTimes = new ArrayList<>();
    for (TimeRange time : relevantTimes) {
      int solsEnd = consolidatedTimes.size()-1;

      if (consolidatedTimes.isEmpty()) {
        consolidatedTimes.add(time);
      }
      else if (!consolidatedTimes.get(solsEnd).overlaps(time)) {      
        consolidatedTimes.add(time);
      }
      else{
        TimeRange currSol = consolidatedTimes.get(solsEnd);
        int startTime = currSol.start();
        int endTime = Math.max(time.end(), currSol.end());
        TimeRange setTime = TimeRange.fromStartEnd(startTime, endTime, false);
        consolidatedTimes.set(solsEnd, setTime); // replace latest busy time
      }
    }
    
    System.out.println("All busy times: " + consolidatedTimes + "\n");

    if (!consolidatedTimes.isEmpty()) {
      // edge case: front 
      int frontEndTime = consolidatedTimes.get(0).start();
      if (frontEndTime != 0) {
        TimeRange newPotential = TimeRange.fromStartEnd(TimeRange.START_OF_DAY, frontEndTime, false);
        if (requestAllMandatory.getDuration() <= newPotential.duration()) {
          solutions.add(newPotential); // replace latest busy time
         }
      }

      // core events 
      for (int i = 1; i < consolidatedTimes.size(); i++) {
        int prevStop = consolidatedTimes.get(i-1).end();
        int currStart = consolidatedTimes.get(i).start();
        TimeRange newPotential = TimeRange.fromStartEnd(prevStop, currStart, false);
        if (requestAllMandatory.getDuration() <= newPotential.duration()) {
          solutions.add(newPotential); // replace latest busy time
        }
        prevStop = currStart;
      }

      // edge case: back
      int backIndex = consolidatedTimes.size()-1;
      int backStartTime = consolidatedTimes.get(backIndex).end();
      if (backStartTime != 1440) {
        TimeRange newPotential = TimeRange.fromStartEnd(backStartTime, TimeRange.END_OF_DAY, true);
        if (requestAllMandatory.getDuration() <= newPotential.duration()) {
          solutions.add(newPotential); // replace latest busy time
        }
      }
    }
    else {
      solutions.add(TimeRange.WHOLE_DAY);
    }

    return solutions;
  }
}
