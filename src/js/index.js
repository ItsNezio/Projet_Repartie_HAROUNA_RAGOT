
const map = L.map('map').setView([48.6833, 6.2], 13); // Coordonnées de Nancy

// Ajouter une couche de tuiles (basemap)
L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
}).addTo(map);

document.addEventListener("DOMContentLoaded", function () {
    showVelib();
});
function showVelib() {
    //Effacer les marqueurs existant
    map.eachLayer(layer => {
        if (layer instanceof L.Marker) {
            map.removeLayer(layer);
        }
    });
    fetch("http://localhost:8000/systemInformation")
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error('Échec de la récupération des données: ' + response.status);
            }
        })
        .then(data => {
            const stationInfo = 'http://localhost:8000/stationInformation'; // URL proxy pour les informations de station
            const stationStatus = 'http://localhost:8000/stationStatus'; // URL proxy pour le statut des stations

            Promise.all([
                fetch(stationInfo).then(res => res.json()),
                fetch(stationStatus).then(res => res.json())
            ])
                .then(([infoData, statusData]) => {
                    const stations = infoData.data.stations.map(station => {
                        const status = statusData.data.stations.find(s => s.station_id === station.station_id);
                        return {
                            station_id: station.station_id,
                            name: station.name,
                            address: station.address,
                            lat: station.lat,
                            lon: station.lon,
                            num_bikes_available: status.num_bikes_available || 0,
                            num_docks_available: status.num_docks_available || 0
                        };
                    });

                    stations.forEach(station => {
                        const { address, lat, lon, name, num_bikes_available, num_docks_available } = station;

                        // Vérifier si les coordonnées sont valides
                        if (!isNaN(lat) && !isNaN(lon)) {
                            const latLng = [lat, lon];

                            // Création du marqueur
                            const marker = L.marker(latLng).addTo(map);
                            const popup = `
                                <strong>Nom: </strong>${name}<br>
                                <strong>Adresse:</strong> ${address}<br>
                                <strong>Vélos disponibles:</strong> ${num_bikes_available}<br>
                                <strong>Places libres:</strong> ${num_docks_available}
                            `;
                            marker.bindPopup(popup);
                        } else {
                            console.error(`Coordonnées invalides pour la station: ${station.station_id}`);
                        }
                    });
                })
                .catch(error => console.error('Erreur lors de la récupération des données:', error));
        })
        .catch(error => console.error('Erreur lors de la récupération des données:', error));
}


function showTraffic() {
    //Effacer les marqueurs existants
    map.eachLayer(layer => {
        if (layer instanceof L.Marker) {
            map.removeLayer(layer);
        }
    });
    fetch("http://localhost:8000/trafic")
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error('Échec de la récupération des données: ' + response.status);
            }
        })
        .then(data => {
            const incidents = data.incidents;
            incidents.forEach(incident => {
                const { type, description, short_description, starttime, endtime, location } = incident;
                const { street, polyline, location_description } = location;

                // Convertir la chaîne polyline en coordonnées lat, lon
                const [lat, lon] = polyline.split(" ").map(Number);

                // Vérifier si les coordonnées sont valides
                if (!isNaN(lat) && !isNaN(lon)) {
                    const latLng = [lat, lon];

                    // Création du marqueur
                    const marker = L.marker(latLng).addTo(map);
                    const popup = `
                        <strong>Type: </strong>${type}<br>
                        <strong>Description: </strong>${description}<br>
                        <strong>Rue: </strong>${street}<br>
                        <strong>Lieu: </strong>${location_description}<br>
                        <strong>Début: </strong>${starttime}<br>
                        <strong>Fin: </strong>${endtime}<br>
                    `;
                    marker.bindPopup(popup);
                } else {
                    console.error(`Coordonnées invalides pour l'incident: ${incident.id}`);
                }
            });
        })
        .catch(error => console.error('Erreur lors de la récupération des données:', error));
}
function showRestaurants() {
    // Effacer les marqueurs existants
    map.eachLayer(layer => {
        if (layer instanceof L.Marker) {
            map.removeLayer(layer);
        }
    });

    fetch("http://localhost:8000/restaurants")
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error('Échec de la récupération des données des restaurants: ' + response.status);
            }
        })
        .then(data => {
            data.forEach(restaurant => {
                const { nom, adresse, latitude, longitude } = restaurant;

                // Remplacer les virgules par des points pour la conversion
                const lat = latitude.replace(',', '.');
                const lon = longitude.replace(',', '.');

                // Vérifier si les coordonnées sont valides
                if (!isNaN(parseFloat(lat)) && !isNaN(parseFloat(lon))) {
                    const latLng = [parseFloat(lat), parseFloat(lon)];

                    // Création du marqueur pour le restaurant
                    const marker = L.marker(latLng).addTo(map);

                    // Création du contenu HTML du popup avec bouton de réservation
                    const popupContent = `
                        <strong>Nom: </strong>${nom}<br>
                        <strong>Adresse:</strong> ${adresse}<br>
                        <button onclick="showReservationForm('${nom}', '${adresse}', '${latitude}', '${longitude}')">Réserver</button>
                    `;

                    marker.bindPopup(popupContent);
                } else {
                    console.error(`Coordonnées invalides pour le restaurant: ${nom}`);
                }
            });
        })
        .catch(error => console.error('Erreur lors de la récupération des données des restaurants:', error));
}

function showReservationForm(nom, adresse, latitude, longitude) {
    // Afficher le formulaire de réservation
    const reservationForm = document.getElementById('reservation-form');
    reservationForm.style.display = 'block';

    document.getElementById('restaurantName').value = nom;
}
function submitReservation(event) {
    event.preventDefault(); // Empêcher le rechargement de la page par défaut

    const restaurantName = document.getElementById('restaurantName').value;
    const nbPersons = document.getElementById('nbPersons').value;
    const lastName = document.getElementById('lastName').value;
    const firstName = document.getElementById('firstName').value;
    const telNumber = document.getElementById('telNumber').value;

    const reservationData = {
        nomRestaurant: restaurantName,
        nbPersonne: nbPersons,
        nomClient: lastName,
        prenomClient: firstName,
        telClient: telNumber
    };

    // Envoyer les données de réservation au serveur
    fetch('http://localhost:8000/restaurants', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(reservationData)
    })
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error('Échec de la réservation: ' + response.status);
            }
        })
        .then(data => {
            console.log('Réservation réussie:', data);
            document.getElementById('reservation-message').textContent = 'Réservation réussie !';
        })
        .catch(error => {
            console.error('Erreur lors de la réservation:', error);
            document.getElementById('reservation-message').textContent = 'Échec de la réservation.';
        });
}

