package com.unicovoit.views.ride;

import com.unicovoit.dto.RideSearchRequestDto;
import com.unicovoit.entity.Ride;
import com.unicovoit.service.RideService;
import com.unicovoit.util.NotificationHelper;
import com.unicovoit.util.SessionManager;
import com.unicovoit.views.layout.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Route(value = "search", layout = MainLayout.class)
@PageTitle("Rechercher un trajet | UniCovoit")
public class SearchView extends VerticalLayout {

    private final RideService rideService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final TextField departureCityField = new TextField();
    private final TextField arrivalCityField = new TextField();
    private final DatePicker datePicker = new DatePicker();
    private final Button searchButton = new Button("Rechercher");

    private final VerticalLayout resultsContainer = new VerticalLayout();
    private List<Ride> searchResults = new ArrayList<>();

    public SearchView(RideService rideService) {
        this.rideService = rideService;

        if (!SessionManager.isLoggedIn()) {
            getUI().ifPresent(ui -> ui.navigate("login"));
            return;
        }

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        add(createSearchCard(), resultsContainer);
    }

    private Div createSearchCard() {
        Div card = new Div();
        card.addClassNames(LumoUtility.Background.BASE, LumoUtility.BoxShadow.MEDIUM, LumoUtility.BorderRadius.LARGE);
        card.getStyle().set("padding", "var(--lumo-space-xl)");

        H2 title = new H2("Rechercher un trajet");
        title.addClassName(LumoUtility.Margin.Top.NONE);

        Paragraph subtitle = new Paragraph("Trouvez le trajet idéal pour votre destination");
        subtitle.addClassName(LumoUtility.TextColor.SECONDARY);

        HorizontalLayout searchForm = createSearchForm();

        card.add(title, subtitle, searchForm);
        return card;
    }

    private HorizontalLayout createSearchForm() {
        HorizontalLayout form = new HorizontalLayout();
        form.setWidthFull();
        form.setAlignItems(Alignment.END);
        form.addClassName(LumoUtility.Gap.MEDIUM);

        departureCityField.setLabel("Ville de départ");
        departureCityField.setPlaceholder("Ex: Paris");
        departureCityField.setPrefixComponent(VaadinIcon.MAP_MARKER.create());
        departureCityField.setRequiredIndicatorVisible(true);
        departureCityField.setWidth("300px");

        arrivalCityField.setLabel("Ville d'arrivée");
        arrivalCityField.setPlaceholder("Ex: Lyon");
        arrivalCityField.setPrefixComponent(VaadinIcon.MAP_MARKER.create());
        arrivalCityField.setRequiredIndicatorVisible(true);
        arrivalCityField.setWidth("300px");

        datePicker.setLabel("Date");
        datePicker.setPlaceholder("Sélectionnez une date");
        datePicker.setMin(LocalDate.now());
        datePicker.setRequiredIndicatorVisible(true);
        datePicker.setWidth("200px");

        searchButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        searchButton.setIcon(VaadinIcon.SEARCH.create());
        searchButton.addClickListener(e -> performSearch());

        form.add(departureCityField, arrivalCityField, datePicker, searchButton);
        return form;
    }

    private void performSearch() {
        if (!validateSearch()) {
            return;
        }

        searchButton.setEnabled(false);
        searchButton.setText("Recherche...");

        try {
            RideSearchRequestDto dto = new RideSearchRequestDto();
            dto.setDepartureCity(departureCityField.getValue().trim());
            dto.setArrivalCity(arrivalCityField.getValue().trim());
            dto.setDate(datePicker.getValue());

            searchResults = rideService.searchRides(dto);
            displayResults();

            searchButton.setEnabled(true);
            searchButton.setText("Rechercher");

        } catch (Exception ex) {
            NotificationHelper.showError("Erreur lors de la recherche: " + ex.getMessage());
            searchButton.setEnabled(true);
            searchButton.setText("Rechercher");
        }
    }

    private boolean validateSearch() {
        boolean valid = true;

        if (departureCityField.isEmpty()) {
            departureCityField.setInvalid(true);
            valid = false;
        }

        if (arrivalCityField.isEmpty()) {
            arrivalCityField.setInvalid(true);
            valid = false;
        }

        if (datePicker.isEmpty()) {
            datePicker.setInvalid(true);
            valid = false;
        }

        if (!valid) {
            NotificationHelper.showWarning("Veuillez remplir tous les champs");
        }

        return valid;
    }

    private void displayResults() {
        resultsContainer.removeAll();

        if (searchResults.isEmpty()) {
            resultsContainer.add(createEmptyState());
            return;
        }

        H3 resultsTitle = new H3(searchResults.size() + " trajet(s) trouvé(s)");
        resultsContainer.add(resultsTitle);

        searchResults.forEach(ride -> resultsContainer.add(createRideCard(ride)));
    }

    private Div createEmptyState() {
        Div emptyState = new Div();
        emptyState.addClassNames(LumoUtility.TextAlignment.CENTER, LumoUtility.Padding.XLARGE);

        Icon icon = VaadinIcon.SEARCH.create();
        icon.setSize("64px");
        icon.addClassName(LumoUtility.TextColor.SECONDARY);

        H3 title = new H3("Aucun trajet trouvé");
        Paragraph text = new Paragraph("Essayez de modifier vos critères de recherche");
        text.addClassName(LumoUtility.TextColor.SECONDARY);

        emptyState.add(icon, title, text);
        return emptyState;
    }

    private Div createRideCard(Ride ride) {
        Div card = new Div();
        card.addClassNames(LumoUtility.Background.BASE, LumoUtility.BoxShadow.SMALL, LumoUtility.BorderRadius.MEDIUM);
        card.getStyle()
                .set("padding", "var(--lumo-space-l)")
                .set("margin-bottom", "var(--lumo-space-m)")
                .set("cursor", "pointer")
                .set("transition", "box-shadow 0.2s");

        card.getElement().addEventListener("mouseenter", e ->
            card.getStyle().set("box-shadow", "var(--lumo-box-shadow-l)")
        );
        card.getElement().addEventListener("mouseleave", e ->
            card.getStyle().set("box-shadow", "var(--lumo-box-shadow-s)")
        );

        card.addClickListener(e -> viewRideDetails(ride));

        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setWidthFull();
        mainLayout.setAlignItems(Alignment.CENTER);

        // Left section - Route info
        VerticalLayout routeInfo = new VerticalLayout();
        routeInfo.setSpacing(false);
        routeInfo.setPadding(false);

        H4 route = new H4(ride.getDepartureCity() + " → " + ride.getArrivalCity());
        route.addClassName(LumoUtility.Margin.NONE);

        Span dateTime = new Span(ride.getDepartureDateTime().format(DATE_FORMATTER));
        dateTime.addClassName(LumoUtility.TextColor.SECONDARY);

        routeInfo.add(route, dateTime);

        // Middle section - Driver info
        VerticalLayout driverInfo = new VerticalLayout();
        driverInfo.setSpacing(false);
        driverInfo.setPadding(false);

        Span driverName = new Span("Conducteur: " + ride.getDriver().getFirstName() + " " + ride.getDriver().getLastName());
        Span vehicle = new Span(ride.getVehicle().getBrand() + " " + ride.getVehicle().getModel());
        vehicle.addClassName(LumoUtility.TextColor.SECONDARY);

        driverInfo.add(driverName, vehicle);

        // Right section - Price and seats
        VerticalLayout priceInfo = new VerticalLayout();
        priceInfo.setSpacing(false);
        priceInfo.setPadding(false);
        priceInfo.setAlignItems(Alignment.END);

        H3 price = new H3(ride.getPricePerSeat() + " €");
        price.addClassNames(LumoUtility.TextColor.PRIMARY, LumoUtility.Margin.NONE);

        Span seats = new Span(ride.getSeatsAvailable() + " place(s) disponible(s)");
        seats.addClassName(LumoUtility.TextColor.SECONDARY);

        priceInfo.add(price, seats);

        mainLayout.add(routeInfo, driverInfo, priceInfo);
        mainLayout.expand(routeInfo, driverInfo);

        card.add(mainLayout);
        return card;
    }

    private void viewRideDetails(Ride ride) {
        getUI().ifPresent(ui -> ui.navigate("rides/" + ride.getId()));
    }
}
