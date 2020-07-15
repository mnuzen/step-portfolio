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


public final class FindMeetingQuery {
  /** MeetingRequest request has:
      getAttendees() method which returns hashSet of mandatory attendees
      getDuration() method which tells us how long our meeting has to be */

  /** Collection<Event> events has events. Each event has:
      getWhen() method which tells us time range of event
      getAttendees() method which tells us who has to go to each event */

  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<String> mandatoryAttendees = request.getAttendees();
    ArrayList<TimeRange> solutions = new ArrayList<>();
    System.out.println("testtttmelba");


    //Set<Event> eventsSet = new HashSet<Event>(events);
    //eventsSet.addAll(events);

    // Iterate through all events, ensuring each event has at least one member from mandatoryAttendees    
    Iterator<Event> iter = events.iterator();
    while (iter.hasNext()) {
      Event event = iter.next();
      Set<String> attendees = event.getAttendees();
      boolean overlap = false;

      for (String attendee : attendees) {
        if (mandatoryAttendees.contains(attendee)){
          overlap = true; // overlap is true if this event has a mandatory attendee
          break;
        }
      }

      if (!overlap) {
        iter.remove(); // remove event if mandatory attendees are not needed
      }   
    }

    // Then condense overlapping ranges in events 
    List<TimeRange> allRanges = Collections.emptyList();
    List<TimeRange> sortedRanges = Collections.emptyList();
    System.out.println("testtttmelba2");

    for (Event event : events) {
      allRanges.add(event.getWhen());
      System.out.println(event.getTitle());
    }

    // Collections.sort(allRanges);



    // and then find the free times in between each time range and check if its big enough


    //throw new UnsupportedOperationException("TODO: Implement this method.");  
    return null;

  }
}
