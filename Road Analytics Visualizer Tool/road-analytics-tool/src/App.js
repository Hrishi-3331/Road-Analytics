import './App.css';
import { useState, useEffect } from 'react';
import ReactMapGL, { Marker, Popup } from 'react-map-gl';

function App() {
  const [viewport, setViewport] = useState({
    latitude: 19.118,
    longitude: 72.991,
    width: "100vw",
    height: "100vh",
    zoom: 12
  });
  const [selectedPark, setSelectedPark] = useState(null);

  //mapbox://styles/hrishi-3331/ckki1prjw0g8p17o6f5x7jh5u

  return (
    <div className="App">
      <div className="top-border">
        <div className="title">Road Analytics Tool</div>
      </div>
      <ReactMapGL
        {...viewport}
        mapboxApiAccessToken="pk.eyJ1IjoiaHJpc2hpLTMzMzEiLCJhIjoiY2p3enpxZ3NqMHdpaDN5b3luMHhsdnlrdCJ9.gLk8ChT7pKjMRfDtrlDNcw"
        mapStyle="mapbox://styles/hrishi-3331/ckki1prjw0g8p17o6f5x7jh5u"
        onViewportChange={viewport => {
          setViewport(viewport);
        }}>

      </ReactMapGL>
    </div>
  );
}

export default App;
