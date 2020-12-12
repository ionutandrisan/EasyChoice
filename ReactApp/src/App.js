import React, { Component } from 'react';
import './App.css';
import InstructorApp from './component/InstructorApp';

class App extends Component {

  
  render() {
    return (
      <div style={{ height: "100%" }}>
        <InstructorApp />
      </div>
    );
  }
}

export default App;
