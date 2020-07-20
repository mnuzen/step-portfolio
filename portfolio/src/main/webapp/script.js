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

/** Chart Data */
google.charts.load('current', {'packages':['corechart']});
google.charts.setOnLoadCallback(drawChart);

/** Fetches sleep data and uses it to create a chart. */
function drawChart() {
  fetch('/sleep-data').then(response => response.json())
  .then((sleepData) => {
    const data = new google.visualization.DataTable();
    data.addColumn('date', 'Day');
    data.addColumn('number', 'Sleep Minutes');
    Object.keys(sleepData).forEach((day) => {
      data.addRow([new Date(day), sleepData[day]]);
    });

    const options = {
      'title': 'Sleep Minutes per Day',
      'width': 800,
      'height': 700,
      'hAxis': {
        format: 'MM/dd/yy',
        gridlines: {color: 'none'},
        title: 'Date'
      },
      'vAxis': {
        title: 'Minutes',
      }
    };

    const chart = new google.visualization.ScatterChart(
        document.getElementById('chart-container'));
    chart.draw(data, options);
  });
}

/**
 * Adds a random greeting to the page.
 */
function addRandomGreeting() {
  const greetings =
      ['Hello world!', '¡Hola Mundo!', '你好，世界！', 'Bonjour le monde!'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('message-container');
  greetingContainer.innerText = greeting;
}

/**
 * Adds a random quote to the page.
 */ 
function getRandomData() {
    fetch('/data') // sends a request to /data
    .then(response => response.json()) // parases response as JSON 
    .then((messages) => {
      // messages is an object, not a string, so we have to
      // reference its fields to create HTML content
  
      const messageElement = document.getElementById('message-container');
      messageElement.innerHTML = '';
      messageElement.appendChild(
          createListElement('First Message: ' + messages[0]));
      messageElement.appendChild(
          createListElement('Second Message: ' + messages[1]));
      messageElement.appendChild(
          createListElement('Third Message: ' + messages[2]));
  });
}

/**
 * Adds PCAP data.
 */ 
function getPCAP() {
  fetch('/PCAP-data') // sends a request to /data
  .then(response => response.json()) // parases response as JSON 
  .then((packets) => {
    // packets is an object, not a string, so we have to
    // reference its fields to create HTML content

    const packetElement = document.getElementById('message-container');
    packetElement.innerHTML = '';
    packetElement.appendChild(
        createListElement('First Packet: ' + packets[0]));
        packetElement.appendChild(
        createListElement('Second Packet: ' + packets[1]));
        packetElement.appendChild(
        createListElement('Third Packet: ' + packets[2]));
});
}

/** Fetches tasks from the server and adds them to the DOM. */
function loadComments() {
  fetch('/data').then(response => response.json()).then((comments) => {
    const commentElement = document.getElementById('Comments');
    comments.forEach((comment) => {
      commentElement.appendChild(createListElement(task));
    })
  });
}

/** Creates an <li> element containing text. */
function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
}