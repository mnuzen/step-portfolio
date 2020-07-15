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
  /** Determines all potential meeting times given a meeting request and a list of events for the day. Trims down list of events to only 
    * hold events with mandatory attendees, then consolidates all busy time ranges into a list of time ranges. From that list of busy times, 
    * pulls out potential solutions based on times that are not busy and still within the scope of the day. */
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<String> mandatoryAttendees = request.getAttendees();

    // Check edge cases
    MeetingRequest requestAllMandatory = new MeetingRequest (mandatoryAttendees, request.getDuration());
    ArrayList<TimeRange> solutions = new ArrayList<>();

    // Requests with duration longer than a day, return no solutions
    if (requestAllMandatory.getDuration() > TimeRange.WHOLE_DAY.duration()){
      return solutions;
    }

    // If there are no attendees or meeting duration is 0, return whole day as solution 
    if (requestAllMandatory.getAttendees().isEmpty() || requestAllMandatory.getDuration() == 0){
      solutions.add(TimeRange.WHOLE_DAY);
      return solutions;
    }

    // Iterate through all events, ensuring each event has at least one member from mandatoryAttendees    
    ArrayList<TimeRange> relevantTimes = retrieveRelevantTimes(events, mandatoryAttendees);

    // Condense overlapping ranges in events (assuming events are sorted)
    ArrayList<TimeRange> consolidatedTimes = consolidateTimes(relevantTimes);

    // Retrieve open intervals based on consolidated times
    solutions = retrieveSolutions(solutions, consolidatedTimes, requestAllMandatory);
    
    return solutions;
  }

  /** Given a series of events and a list of mandatory attendees, trims down list of events until the only events remaining are ones
    * that require mandatory attendees.  */
  private ArrayList<TimeRange> retrieveRelevantTimes (Collection<Event> events, Collection<String> mandatoryAttendees) {
    ArrayList<TimeRange> relevantTimes = new ArrayList<>();

    Iterator<Event> iter = events.iterator();
    while (iter.hasNext()) {
      Event event = iter.next();
      Set<String> attendees = event.getAttendees();

      for (String attendee : attendees) {
        if (mandatoryAttendees.contains(attendee)){
          relevantTimes.add(event.getWhen()); // adding time if this event has a mandatory attendee
          break;
        }
      }
    }

    return relevantTimes;
  }

  /** Given a list of busy times, consolidates the time ranges that overlap to minimize time ranges. */
  private ArrayList<TimeRange> consolidateTimes (ArrayList<TimeRange> relevantTimes) {
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

    return consolidatedTimes;
  }

  /** Given a list of consolidated time ranges, retrieves solutions and potential free times based on the 24-hour interval that are not 
    * overlapping with the busy times.*/
  private ArrayList<TimeRange> retrieveSolutions (ArrayList<TimeRange> solutions, 
                                                  ArrayList<TimeRange> consolidatedTimes, MeetingRequest requestAllMandatory) {
    if (!consolidatedTimes.isEmpty()) {
      // edge case: if there's time between the start of the day and the start of the first event 
      int frontIndex = 0;
      int frontEndTime = consolidatedTimes.get(frontIndex).start();
      if (frontEndTime != TimeRange.START_OF_DAY) {
        TimeRange newPotential = TimeRange.fromStartEnd(TimeRange.START_OF_DAY, frontEndTime, false);
        if (requestAllMandatory.getDuration() <= newPotential.duration()) {
          solutions.add(newPotential); // replace latest busy time
         }
      }

      // retrieve free times in between busy intervals 
      for (int i = 1; i < consolidatedTimes.size(); i++) {
        int prevStop = consolidatedTimes.get(i-1).end();
        int currStart = consolidatedTimes.get(i).start();
        TimeRange newPotential = TimeRange.fromStartEnd(prevStop, currStart, false);
        if (requestAllMandatory.getDuration() <= newPotential.duration()) {
          solutions.add(newPotential); // replace latest busy time
        }
        prevStop = currStart;
      }

      // edge case: if there's time beteween the end of the last event and the end of the day
      int backIndex = consolidatedTimes.size()-1;
      int backStartTime = consolidatedTimes.get(backIndex).end();
      if (backStartTime != TimeRange.END_OF_DAY+1) {
        TimeRange newPotential = TimeRange.fromStartEnd(backStartTime, TimeRange.END_OF_DAY, true);
        if (requestAllMandatory.getDuration() <= newPotential.duration()) {
          solutions.add(newPotential); // replace latest busy time
        }
      }
    }
    else {
      // if there are no consolidated events, the whole day is free
      solutions.add(TimeRange.WHOLE_DAY);
    }

    return solutions;
  }

}
