import React from "react";

export function TooltipContent ({ country, artists }) {
    return (<>
        <h4>{ country }</h4>

        <ul>{
            artists.map(artist => (
                <li key={artist.id}>{ artist.name }</li>
            ))
        }</ul>
    </>)
}