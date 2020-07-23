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

/** Create network graph one. */
function createNetworkOne(){
  var color = "gray";
  var len = undefined;

  var nodes = [
    { id: 0, label: "My Computer", group: 0 },
    { id: 4, label: "Class C", group: 6 },
    { id: 7, label: "Class A", group: 8 },
    { id: 13, label: "Class E", group: 7 },
    { id: 15, label: "Class D", group: 5 },
    { id: 16, label: "224.0.0.0", group: 5 },
    { id: 17, label: "225.0.0.1", group: 5 },
    { id: 18, label: "192.1.1.1", group: 6 },
    { id: 19, label: "192.0.1.1", group: 6 },
    { id: 21, label: "240.0.0.0", group: 7 },
    { id: 22, label: "241.0.0.0", group: 7 },
    { id: 23, label: "242.0.0.0", group: 7 },
    { id: 30, label: "242.0.0.1", group: 7 },
    { id: 31, label: "242.0.1.1", group: 7 },
    { id: 24, label: "1.0.0.1", group: 8 },
    { id: 25, label: "1.1.1.1", group: 8 },
    { id: 26, label: "126.1.1.1", group: 8 },
    { id: 27, label: "128.2.0.1", group: 9 },
    { id: 28, label: "Class B", group: 9 },
    { id: 29, label: "128.1.0.1", group: 9 },
    { id: 32, label: "128.1.1.1", group: 9 },
    { id: 33, label: "128.1.1.2", group: 9 },
    { id: 34, label: "128.1.2.2", group: 9 },
  ];

  var edges = [
    { from: 1, to: 0 },
    { from: 2, to: 0 },
    { from: 4, to: 3 },
    { from: 5, to: 4 },
    { from: 4, to: 0 },
    { from: 7, to: 6 },
    { from: 8, to: 7 },
    { from: 7, to: 0 },
    { from: 10, to: 9 },
    { from: 11, to: 10 },
    { from: 10, to: 4 },
    { from: 13, to: 12 },
    { from: 14, to: 13 },
    { from: 13, to: 0 },
    { from: 16, to: 15 },
    { from: 17, to: 15 },
    { from: 15, to: 0 },
    { from: 19, to: 18 },
    { from: 19, to: 18 },
    { from: 19, to: 18 },
    { from: 20, to: 19 },
    { from: 19, to: 4 },
    { from: 13, to: 21 },
    { from: 23, to: 13 },
    { from: 23, to: 30 },
    { from: 23, to: 31 },
    { from: 22, to: 13 },
    { from: 25, to: 24 },
    { from: 26, to: 25 },
    { from: 25, to: 7 },
    { from: 28, to: 27 },
    { from: 29, to: 28 },
    { from: 29, to: 28 },
    { from: 29, to: 28 },
    { from: 29, to: 32 },
    { from: 32, to: 33 },
    { from: 34, to: 33 },
    { from: 28, to: 0 },
  ];

  // create a network
  var container = document.getElementById("mynetwork");
  var data = {
    nodes: nodes,
    edges: edges,
  };
  var options = {
    nodes: {
      shape: "dot",
      size: 30,
      font: {
        size: 32,
        color: "#ffffff",
      },
      borderWidth: 2,
    },
    edges: {
      width: 2,
    },
  };

  network = new vis.Network(container, data, options);
}


/** Create network graph two. */
function createNetworkTwo(){
  var color = "gray";
  var len = undefined;

  var nodes = [
    { id: 0, label: "My Computer", group: 0 },
    
    { id: 4, label: "Class C", group: 6 },
    { id: 7, label: "Class A", group: 8 },
   
    { id: 13, label: "Class E", group: 7 },
    { id: 15, label: "Class D", group: 5 },
    { id: 16, label: "224.0.0.0", group: 5 },
    { id: 17, label: "224.0.0.1", group: 5 },
    { id: 18, label: "193.0.1.1", group: 6 },
    { id: 19, label: "192.0.1.1", group: 6 },
    { id: 21, label: "240.0.0.0", group: 7 },
    { id: 22, label: "241.0.0.0", group: 7 },
    { id: 23, label: "242.0.0.0", group: 7 },
    { id: 24, label: "1.0.0.1", group: 8 },
    { id: 25, label: "1.1.1.1", group: 8 },
    { id: 26, label: "126.1.1.1", group: 8 },
    { id: 27, label: "128.2.0.1", group: 9 },
    { id: 28, label: "Class B", group: 9 },
    { id: 29, label: "128.1.0.1", group: 9 },
  ];
  var edges = [
    { from: 1, to: 0 },
    { from: 2, to: 0 },
    { from: 4, to: 3 },
    { from: 5, to: 4 },
    { from: 4, to: 0 },
    { from: 7, to: 6 },
    { from: 8, to: 7 },
    { from: 7, to: 0 },
    { from: 10, to: 9 },
    { from: 11, to: 10 },
    { from: 10, to: 4 },
    { from: 13, to: 12 },
    { from: 14, to: 13 },
    { from: 13, to: 0 },
    { from: 16, to: 15 },
    { from: 17, to: 15 },
    { from: 15, to: 0 },
    { from: 19, to: 18 },
    { from: 19, to: 18 },
    { from: 19, to: 18 },
    { from: 20, to: 19 },
    { from: 19, to: 4 },
    { from: 22, to: 21 },
    { from: 23, to: 22 },
    { from: 22, to: 13 },
    { from: 25, to: 24 },
    { from: 26, to: 25 },
    { from: 25, to: 7 },
    { from: 28, to: 27 },
    { from: 29, to: 28 },
    { from: 29, to: 28 },
    { from: 29, to: 28 },
    { from: 28, to: 0 },
  ];


  // create a network
  var container = document.getElementById("mynetwork");
  var data = {
    nodes: nodes,
    edges: edges,
  };
  var options = {
    nodes: {
      shape: "dot",
      size: 30,
      font: {
        size: 32,
        color: "#ffffff",
      },
      borderWidth: 2,
    },
    edges: {
      width: 2,
    },
  };

  network = new vis.Network(container, data, options);
}
