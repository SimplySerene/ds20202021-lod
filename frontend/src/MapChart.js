import React from "react";
import {
    ZoomableGroup,
    ComposableMap,
    Geographies,
    Geography
} from "react-simple-maps";
import { scaleLinear } from "d3-scale";

const geoUrl =
    "https://raw.githubusercontent.com/zcreativelabs/react-simple-maps/master/topojson-maps/world-110m.json";

const MapChart = ({ setTooltipContent, artistsPerCountry }) => {
    const maxArtistsPerCountry = Math.max(...Object.values(artistsPerCountry).map(artists => artists.length))
    const colorScale = scaleLinear()
        .domain([0, maxArtistsPerCountry])
        .range(["#ffedea", "#ff5233"]);

    return (
        <>
            <ComposableMap data-tip="" projectionConfig={{ scale: 200 }}>
                <ZoomableGroup>
                    <Geographies geography={geoUrl}>
                        {({ geographies }) =>
                            geographies.map(geo => {
                                const code = geo.properties.ISO_A2
                                const artists = artistsPerCountry[code] || []

                                return (
                                    <Geography
                                        key={geo.rsmKey}
                                        geography={geo}
                                        onMouseEnter={() => {
                                            setTooltipContent(<ul>{
                                                artists.map(artist => (
                                                    <li key={artist.id}>{ artist.name }</li>
                                                ))
                                            }</ul>);
                                        }}
                                        onMouseLeave={() => {
                                            setTooltipContent("");
                                        }}
                                        fill={artists.length > 0 ? colorScale(artists.length) : "#F5F4F6"}
                                    />
                                )
                            })
                        }
                    </Geographies>
                </ZoomableGroup>
            </ComposableMap>
        </>
    );
};

export default MapChart;
