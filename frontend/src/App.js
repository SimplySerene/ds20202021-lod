import { useState, useEffect } from "react";
import ReactTooltip from "react-tooltip";

import './App.css';
import MapChart from "./MapChart";
import {PlaylistSearch} from "./PlaylistSearch";
import {Playlist} from "./Playlist";

function App() {
  const [content, setContent] = useState("");
  const [loading, setLoading] = useState(false);
  const [playlist, setPlaylist] = useState(undefined)
  const [artistsPerCountry, setArtistsPerCountry] = useState({});
  const [artists, setArtists] = useState([]);
  const [numArtistResolved, setNumArtistsResolved] = useState(0);

  useEffect(function () {
      (async function onPlaylistSelected() {
          if (!playlist || !playlist.id) {
              return
          }

          setLoading(true)

          const response = await fetch(`/api/playlist-info/${playlist.id}`)
          const results = await response.json()

          const newArtistsPerCountry = {}
          let newNumArtistResolved = 0

          for (const enhancedArtist of results) {
              for (const countryCode of (enhancedArtist.lodInfo.countryCodes || [])) {
                  if (!newArtistsPerCountry[countryCode]) {
                      newArtistsPerCountry[countryCode] = []
                  }
                  newArtistsPerCountry[countryCode].push(enhancedArtist.spotifyArtist)
                  newNumArtistResolved++
              }
          }

          setArtists(results)
          setNumArtistsResolved(newNumArtistResolved)
          setArtistsPerCountry(newArtistsPerCountry)
          setLoading(false)
      })()
  }, [playlist])

  return (
    <div className="map-container">
        <h1 style={{textAlign: 'center'}}>{
            playlist ? playlist.name : 'Linked Open Spotify'
        }</h1>

        <PlaylistSearch onSuggestionSelected={(event, { suggestion }) => setPlaylist(suggestion)} />

        <div style={{marginTop: '15px'}}></div>

        { playlist ? <Playlist playlist={playlist} /> : null }

        { loading ? "Loading (this might take a while)... " : (<>
            <MapChart setTooltipContent={setContent} artistsPerCountry={artistsPerCountry} />
            <ReactTooltip>{content}</ReactTooltip>
        </>) }

        { playlist && !loading && artists && artistsPerCountry ? <>
            <p>{numArtistResolved} / {artists.length} artists were used</p>
        </> : null}
    </div>
  );
}

export default App;
