import Autosuggest from 'react-autosuggest'
import AutosuggestHighlightMatch from 'autosuggest-highlight/match/index'
import AutosuggestHighlightParse from 'autosuggest-highlight/parse/index'
import { useState } from 'react'
import { PlaylistImage } from './PlaylistImage'
import debounce from 'lodash.debounce'
import './PlaylistSearch.css'

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

const fetchSuggestions = debounce(async function fetchSuggestions(query, setSuggestions) {
    if (!query || query.length <=2 ) {
        return
    }

    const response = await fetch(`/api/playlist-search?query=${query}`)
    const results = await response.json()

    if (!Array.isArray(results)) {
        setSuggestions([])
        return
    }

    setSuggestions(results)
}, 500)

export function PlaylistSearch({ onSuggestionSelected }) {
    const [query, setQuery] = useState("");
    const [suggestions, setSuggestions] = useState([]);

    return (
        <Autosuggest
            suggestions={suggestions}
            onSuggestionsFetchRequested={() => {fetchSuggestions(query, setSuggestions)}}
            onSuggestionsClearRequested={() => setSuggestions([])}
            getSuggestionValue={suggestion => suggestion.name}
            renderSuggestion={(suggestion, { query }) => (
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