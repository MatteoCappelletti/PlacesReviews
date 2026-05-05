function ottieniPosizione() {
    const display = document.getElementById("output");
    const latitudine_input = document.getElementById("latitudesearch");
    const longitude_input = document.getElementById("longitudesearch");

    if (!navigator.geolocation) {
        display.innerHTML = "La geolocalizzazione non è supportata dal tuo browser.";
        return;
    }

    display.innerHTML = "Localizzazione in corso...";

    navigator.geolocation.getCurrentPosition(successo, errore);

    function successo(posizione) {
        const lat = posizione.coords.latitude;
        const lon = posizione.coords.longitude;

        display.innerHTML = "Latitudine: " + lat.toFixed(5) + "<br> Longitudine: " + lon.toFixed(5);

        console.log("Coordinate ottenute: Lat " + lat + ", Lon " + lon);

        latitudine_input.value = lat;
        longitude_input.value = lon;

        document.getElementById("geo-form").submit();
    }

    function errore() {
        display.innerHTML = "Impossibile recuperare la tua posizione (permesso negato o errore di rete).";
    }
}

async function trovaCoordinate() {
    const citta = document.getElementById('city').value;
    console.log(citta)
    const via = document.getElementById('address').value;
    console.log(via)
    const inputLat = document.getElementById('latitude');
    const inputLng = document.getElementById('longitude');
    const divStato = document.getElementById('stato');

    // Reset campi e messaggi
    inputLat.value = "";
    inputLng.value = "";
    divStato.innerText = "Ricerca in corso...";
    divStato.style.color = "#666";

    if (!citta || !via) {
        divStato.innerText = "Inserisci sia città che indirizzo!";
        divStato.style.color = "red";
        return;
    }

    const query = encodeURIComponent(via + ", " + citta);
    const url = "https://nominatim.openstreetmap.org/search?format=json&q=$" + query +"&limit=1";

    try {
        const response = await fetch(url, {
            headers: { 'Accept-Language': 'it' }
        });
        const data = await response.json();

        if (data && data.length > 0) {
            // Compilazione automatica dei campi
            inputLat.value = data[0].lat;
            inputLng.value = data[0].lon;

            divStato.innerText = "Coordinate caricate con successo!";
            divStato.style.color = "green";
        } else {
            divStato.innerText = "Indirizzo non trovato.";
            divStato.style.color = "orange";
        }
    } catch (error) {
        divStato.innerText = "Errore di connessione.";
        divStato.style.color = "red";
    }
}