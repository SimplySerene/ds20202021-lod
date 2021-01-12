export function PlaylistImage({ playlist, size }) {
    size = size || 35;

    if (Array.isArray(playlist.images) && playlist.images.length > 0) {
        return (<img
            alt={playlist.name}
            src={playlist.images[0].url}
            height={size} width={size}
        />)
    }

    return (<div style={{
        width: `${size}px`,
        height: `${size}px`,
        backgroundColor: 'whitesmoke',
        border: '1px dashed black',
    }}></div>)
}
