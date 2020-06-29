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


/** Creates an <li> element containing text. */
function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
}