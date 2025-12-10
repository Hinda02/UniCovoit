package com.unicovoit.views.rides;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.unicovoit.dto.RideSearchRequestDto;
import com.unicovoit.entity.Ride;
import com.unicovoit.service.RideService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("results")
@PageTitle("Résultats de recherche | UniCovoit")
public class SearchResultsView extends VerticalLayout {

    private final RideService rideService;

    private final TextField departureField = new TextField("Départ");
    private final TextField arrivalField   = new TextField("Arrivée");
    private final DatePicker dateField     = new DatePicker("Date");
    private final Button searchButton      = new Button("Rechercher");

    private final VerticalLayout resultsLayout = new VerticalLayout();

    @Autowired
    public SearchResultsView(RideService rideService) {
        this.rideService = rideService;

        setWidthFull();
        setPadding(true);
        setSpacing(true);

        H2 title = new H2("Recherche de trajets");

        // Search bar
        HorizontalLayout searchBar = new HorizontalLayout(departureField, arrivalField, dateField, searchButton);
        searchBar.setWidthFull();
        searchButton.addClickListener(e -> performSearch());

        resultsLayout.setSpacing(true);
        resultsLayout.setWidthFull();

        add(title, searchBar, resultsLayout);
    }

    private void performSearch() {
        try {
            RideSearchRequestDto dto = new RideSearchRequestDto();
            dto.setDepartureCity(departureField.getValue());
            dto.setArrivalCity(arrivalField.getValue());
            dto.setDate(dateField.getValue());

            List<Ride> rides = rideService.searchRides(dto);

            resultsLayout.removeAll();

            if (rides.isEmpty()) {
                resultsLayout.add(new Paragraph("Aucun trajet trouvé."));
                return;
            }

            for (Ride ride : rides) {
                resultsLayout.add(createRideCard(ride));
            }

        } catch (IllegalArgumentException ex) {
            Notification.show(ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            Notification.show("Erreur lors de la recherche.");
        }
    }

    private VerticalLayout createRideCard(Ride ride) {

        VerticalLayout card = new VerticalLayout();
        card.setPadding(true);
        card.setSpacing(false);
        card.setWidthFull();
        card.getStyle().set("border", "1px solid #e5e7eb");
        card.getStyle().set("border-radius", "8px");

        Paragraph cities = new Paragraph(
                ride.getDepartureCity() + " → " + ride.getArrivalCity()
        );

        Paragraph dateTime = new Paragraph(
                "Départ : " + ride.getDepartureDateTime().toString()
        );

        Paragraph price = new Paragraph(
                ride.getPricePerSeat() + " € / personne"
        );

        Paragraph seats = new Paragraph(
                ride.getSeatsAvailable() + " places disponibles"
        );

        card.add(cities, dateTime, price, seats);

        return card;
    }
}
