import { useState } from "react";
import ReactTooltip from "react-tooltip";

import './App.css';
import MapChart from "./MapChart";
import {PlaylistSearch} from "./PlaylistSearch";
import {Playlist} from "./Playlist";

function App() {
  const [content, setContent] = useState("");
  const [playlist, setPlaylist] = useState(undefined)

  return (
    <div className="map-container">
        <h1 style={{textAlign: 'center'}}>{
            playlist ? playlist.name : 'Linked Open Spotify'
        }</h1>

        <PlaylistSearch onSuggestionSelected={(event, { suggestion }) => {
            setPlaylist(suggestion)
        }} />

        <div style={{marginTop: '15px'}}></div>

        { playlist ? <Playlist playlist={playlist} /> : null }

        <MapChart setTooltipContent={setContent} />
        <ReactTooltip>{content}</ReactTooltip>
    </div>
  );
}

export default App;
