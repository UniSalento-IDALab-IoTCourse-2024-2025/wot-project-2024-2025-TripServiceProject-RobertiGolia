# Taxi Sociale App – Progetto IoT 2024/2025

## Descrizione del progetto

Questo progetto nasce con l'obiettivo di sviluppare una piattaforma intelligente e user-friendly per la gestione e la prenotazione di corse condivise, pensata per favorire la mobilità sostenibile e la collaborazione tra utenti.

L'applicazione consente agli utenti di:

- Registrarsi come passeggeri o autisti;
- Prenotare corse disponibili;
- Visualizzare in tempo reale lo stato delle proprie prenotazioni;
- Interagire tramite chatbot;
- Gestire il proprio profilo personale.

La soluzione è progettata per garantire:

- Facilità d'uso, con un'interfaccia intuitiva e accessibile;
- Sicurezza nella gestione dei dati personali e delle transazioni;
- Scalabilità, per supportare un numero crescente di utenti e corse;
- Integrazione con servizi di notifica, via email, per aggiornamenti in tempo reale.

## Come funziona TripServiceProject

**TripServiceProject** è il microservizio responsabile della gestione delle corse all'interno della piattaforma Taxi Sociale. Le sue principali funzionalità includono:

- **Gestione corse**: consente la creazione, la prenotazione e la visualizzazione delle corse disponibili tra punto A e punto B tramite endpoint REST dedicati.
- **Prenotazione**: permette agli utenti di prenotare una corsa, verificando la disponibilità di posti e aggiornando lo stato della corsa in tempo reale.
- **Visualizzazione stato**: gli utenti possono consultare lo stato delle proprie prenotazioni e delle corse attive.
- **Integrazione con UserServiceProject**: comunica con il microservizio utenti per verificare l'identità e il ruolo degli utenti, nonché per gestire le associazioni tra corse e autisti/passeggeri.
- **Sicurezza**: protegge le API tramite autenticazione JWT, garantendo che solo gli utenti autorizzati possano accedere alle risorse protette.
- **Notifiche**: invia notifiche via email agli utenti per aggiornamenti sulle corse e sulle prenotazioni.

### Flusso tipico di gestione corsa

1. **Creazione corsa**: un utente crea una nuova corsa specificando punto di partenza, destinazione, orario e posti disponibili tramite l'endpoint `/api/trips/`.
2. **Prenotazione**: un utente prenota un posto in una corsa disponibile tramite `/api/trip/reserve`, ricevendo conferma e aggiornamenti via email.
3. **Visualizzazione**: sia autisti che passeggeri possono visualizzare lo stato delle proprie corse e prenotazioni tramite endpoint dedicati.
4. **Cancellazione**: è possibile cancellare una prenotazione o una corsa, con controlli per evitare la perdita di dati rilevanti.

### Tecnologie principali
- Spring Boot
- Spring Security (JWT)
- MongoDB
- REST API

Questa architettura garantisce sicurezza, scalabilità e facilità di integrazione con gli altri componenti della piattaforma Taxi Sociale.