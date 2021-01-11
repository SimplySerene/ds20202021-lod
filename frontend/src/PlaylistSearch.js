import Autosuggest from 'react-autosuggest'
import AutosuggestHighlightMatch from 'autosuggest-highlight/match/index'
import AutosuggestHighlightParse from 'autosuggest-highlight/parse/index'
import { useState } from 'react'
import './PlaylistSearch.css'

function PlaylistImage({ playlist }) {
    const size = 35;

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
    }}></div>)
}

function PlaylistSearchResult({ suggestion, query }) {
    const name = suggestion.name
    const matches = AutosuggestHighlightMatch(name, query);
    const parts = AutosuggestHighlightParse(name, matches);

    return (<div style={{display: 'flex', flexDirection: 'row', alignItems: 'center'}}>
        <PlaylistImage playlist={suggestion} />

        <span style={{marginLeft: '20px'}}>
        {
            parts.map((part, index) => {
                const className = part.highlight ? 'highlight' : null;

                return (
                    <span className={className} key={index}>{part.text}</span>
                );
            })
        }
      </span>
    </div>)
}

export function PlaylistSearch({ onSuggestionSelected }) {
    const [query, setQuery] = useState("");
    const [suggestions, setSuggestions] = useState([]);

    async function fetchSuggestions() {
        const response = await fetch(`/api/playlist-search?query=${query}`)
        const results = await response.json()

        if (!Array.isArray(results)) {
            setSuggestions([])
            return
        }

        setSuggestions(results)
    }

    return (
        <Autosuggest
            suggestions={suggestions}
            onSuggestionsFetchRequested={fetchSuggestions}
            onSuggestionsClearRequested={() => setSuggestions([])}
            getSuggestionValue={suggestion => suggestion.name}
            renderSuggestion={suggestion => (
                <PlaylistSearchResult suggestion={suggestion} query={query} />
            )}
            inputProps={{
                onChange: (event, { newValue }) => setQuery(newValue),
                placeholder: 'Search for a playlist name...',
                value: query,
            }}
            onSuggestionSelected={onSuggestionSelected}
        />
    );
}