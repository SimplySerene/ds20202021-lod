import {PlaylistImage} from "./PlaylistImage";

export function Playlist({ playlist }) {
    return (<div style={{display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center'}}>
        <span>Id: <code>{ playlist.id }</code></span>
        <br/>
        <PlaylistImage playlist={playlist} size={100} />
    </div>)
}