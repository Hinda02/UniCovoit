package com.unicovoit.views.ride;

import com.unicovoit.dto.CreateRideDto;
import com.unicovoit.entity.Vehicle;
import com.unicovoit.service.RideService;
import com.unicovoit.service.VehicleService;
import com.unicovoit.util.NotificationHelper;
import com.unicovoit.util.SessionManager;
import com.unicovoit.views.layout.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Route(value = "create-ride", layout = MainLayout.class)
@PageTitle("Proposer un trajet | UniCovoit")
public class CreateRideView extends VerticalLayout {

    private final RideService rideService;
    private final VehicleService vehicleService;

    private final Select<Vehicle> vehicleSelect = new Select<>();
    private final TextField departureCityField = new TextField("Ville de départ");
    private final TextField departureAddressField = new TextField("Adresse de départ (optionnel)");
    private final TextField arrivalCityField = new TextField("Ville d'arrivée");
    private final TextField arrivalAddressField = new TextField("Adresse d'arrivée (optionnel)");
    private final DateTimePicker departureDateTimeField = new DateTimePicker("Date et heure de départ");
    private final IntegerField durationField = new IntegerField("Durée estimée (minutes)");
    private final NumberField priceField = new NumberField("Prix par place (€)");
    private final IntegerField seatsField = new IntegerField("Nombre de places disponibles");
    private final TextArea descriptionField = new TextArea("Description");
    private final Checkbox musicCheckbox = new Checkbox("Musique autorisée");
    private final Checkbox petsCheckbox = new Checkbox("Animaux autorisés");
    private final Checkbox smokingCheckbox = new Checkbox("Fumeur accepté");

    private final Button saveButton = new Button("Publier le trajet");
    private final Button cancelButton = new Button("Annuler");

    public CreateRideView(RideService rideService, VehicleService vehicleService) {
        this.rideService = rideService;
        this.vehicleService = vehicleService;

        if (!SessionManager.isLoggedIn()) {
            getUI().ifPresent(ui -> ui.navigate("login"));
            return;
        }

        setSizeFull();
        setPadding(true);
        setSpacing(true);
        setAlignItems(Alignment.CENTER);

        add(createFormCard());
        loadVehicles();
    }

    private Div createFormCard() {
        Div card = new Div();
        card.addClassNames(LumoUtility.Background.BASE, LumoUtility.BoxShadow.LARGE, LumoUtility.BorderRadius.LARGE);
        card.setWidth("800px");
        card.getStyle().set("padding", "var(--lumo-space-xl)");

        H2 title = new H2("Proposer un trajet");
        title.addClassName(LumoUtility.Margin.Top.NONE);

        Paragraph subtitle = new Paragraph("Partagez votre trajet et voyagez ensemble");
        subtitle.addClassName(LumoUtility.TextColor.SECONDARY);

        FormLayout formLayout = createForm();
        HorizontalLayout actions = createActions();

        card.add(title, subtitle, formLayout, actions);
        return card;
    }

    private FormLayout createForm() {
        FormLayout form = new FormLayout();
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("600px", 2)
        );

        // Vehicle select
        vehicleSelect.setLabel("Véhicule");
        vehicleSelect.setPlaceholder("Sélectionnez un véhicule");
        vehicleSelect.setItemLabelGenerator(v -> v.getBrand() + " " + v.getModel() + " (" + v.getPlateNumber() + ")");
        vehicleSelect.setRequiredIndicatorVisible(true);

        // Departure fields
        departureCityField.setPlaceholder("Ex: Paris");
        departureCityField.setPrefixComponent(VaadinIcon.MAP_MARKER.create());
        departureCityField.setRequired(true);

        departureAddressField.setPlaceholder("Ex: Gare du Nord");
        departureAddressField.setPrefixComponent(VaadinIcon.LOCATION_ARROW.create());

        // Arrival fields
        arrivalCityField.setPlaceholder("Ex: Lyon");
        arrivalCityField.setPrefixComponent(VaadinIcon.MAP_MARKER.create());
        arrivalCityField.setRequired(true);

        arrivalAddressField.setPlaceholder("Ex: Gare Part-Dieu");
        arrivalAddressField.setPrefixComponent(VaadinIcon.LOCATION_ARROW.create());

        // Date time picker
        departureDateTimeField.setMin(LocalDateTime.now());
        departureDateTimeField.setRequiredIndicatorVisible(true);

        // Duration field
        durationField.setPlaceholder("Ex: 120");
        durationField.setPrefixComponent(VaadinIcon.CLOCK.create());
        durationField.setMin(1);
        durationField.setStepButtonsVisible(true);
        durationField.setHelperText("Durée en minutes");

        // Price field
        priceField.setPlaceholder("Ex: 15.00");
        priceField.setPrefixComponent(VaadinIcon.EURO.create());
        priceField.setMin(0);
        priceField.setMax(999.99);
        priceField.setStep(0.50);
        priceField.setRequiredIndicatorVisible(true);
        priceField.setHelperText("Prix par passager");

        // Seats field
        seatsField.setPlaceholder("Ex: 3");
        seatsField.setPrefixComponent(VaadinIcon.GROUP.create());
        seatsField.setMin(1);
        seatsField.setMax(8);
        seatsField.setStepButtonsVisible(true);
        seatsField.setRequiredIndicatorVisible(true);
        seatsField.setValue(3);

        // Description
        descriptionField.setPlaceholder("Ajoutez des détails sur votre trajet...");
        descriptionField.setMaxLength(5000);
        descriptionField.setHelperText("0/5000 caractères");
        descriptionField.addValueChangeListener(e ->
            descriptionField.setHelperText(e.getValue().length() + "/5000 caractères")
        );

        // Preferences
        HorizontalLayout preferences = new HorizontalLayout(musicCheckbox, petsCheckbox, smokingCheckbox);
        preferences.addClassName(LumoUtility.Gap.MEDIUM);

        form.add(vehicleSelect, 2);
        form.add(departureCityField, departureAddressField);
        form.add(arrivalCityField, arrivalAddressField);
        form.add(departureDateTimeField, durationField);
        form.add(priceField, seatsField);
        form.add(descriptionField, 2);
        form.add(new H4("Préférences"), 2);
        form.add(preferences, 2);

        return form;
    }

    private HorizontalLayout createActions() {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setWidthFull();
        actions.setJustifyContentMode(JustifyContentMode.END);
        actions.addClassName(LumoUtility.Gap.MEDIUM);

        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.setIcon(VaadinIcon.CLOSE.create());
        cancelButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("")));

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        saveButton.setIcon(VaadinIcon.CHECK.create());
        saveButton.addClickListener(e -> createRide());

        actions.add(cancelButton, saveButton);
        return actions;
    }

    private void loadVehicles() {
        try {
            List<Vehicle> vehicles = vehicleService.getUserVehicles(SessionManager.getCurrentUserId());

            if (vehicles.isEmpty()) {
                NotificationHelper.showWarning("Vous devez d'abord ajouter un véhicule");
                getUI().ifPresent(ui -> ui.navigate("vehicles/new"));
                return;
            }

            vehicleSelect.setItems(vehicles);
            vehicleSelect.setValue(vehicles.get(0));

        } catch (Exception ex) {
            NotificationHelper.showError("Erreur lors du chargement des véhicules: " + ex.getMessage());
        }
    }

    private void createRide() {
        if (!validateForm()) {
            return;
        }

        saveButton.setEnabled(false);
        saveButton.setText("Publication...");

        try {
            CreateRideDto dto = new CreateRideDto();
            dto.setVehicleId(vehicleSelect.getValue().getId());
            dto.setDepartureCity(departureCityField.getValue().trim());
            dto.setDepartureAddress(departureAddressField.getValue());
            dto.setArrivalCity(arrivalCityField.getValue().trim());
            dto.setArrivalAddress(arrivalAddressField.getValue());
            dto.setDepartureDateTime(departureDateTimeField.getValue());
            dto.setDurationMinutes(durationField.getValue());
            dto.setPricePerSeat(BigDecimal.valueOf(priceField.getValue()));
            dto.setSeatsTotal(seatsField.getValue());
            dto.setDescription(descriptionField.getValue());
            dto.setMusicEnabled(musicCheckbox.getValue());
            dto.setPetsAllowed(petsCheckbox.getValue());
            dto.setSmokingAllowed(smokingCheckbox.getValue());

            rideService.createRide(dto, SessionManager.getCurrentUser());

            NotificationHelper.showSuccess("Trajet publié avec succès !");
            getUI().ifPresent(ui -> ui.navigate("my-rides"));

        } catch (Exception ex) {
            NotificationHelper.showError(ex.getMessage());
            saveButton.setEnabled(true);
            saveButton.setText("Publier le trajet");
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        if (vehicleSelect.isEmpty()) {
            NotificationHelper.showWarning("Veuillez sélectionner un véhicule");
            valid = false;
        }

        if (departureCityField.isEmpty()) {
            departureCityField.setInvalid(true);
            departureCityField.setErrorMessage("La ville de départ est obligatoire");
            valid = false;
        }

        if (arrivalCityField.isEmpty()) {
            arrivalCityField.setInvalid(true);
            arrivalCityField.setErrorMessage("La ville d'arrivée est obligatoire");
            valid = false;
        }

        if (departureDateTimeField.isEmpty()) {
            departureDateTimeField.setInvalid(true);
            departureDateTimeField.setErrorMessage("La date de départ est obligatoire");
            valid = false;
        }

        if (priceField.isEmpty()) {
            priceField.setInvalid(true);
            priceField.setErrorMessage("Le prix est obligatoire");
            valid = false;
        }

        if (seatsField.isEmpty() || seatsField.getValue() < 1) {
            seatsField.setInvalid(true);
            seatsField.setErrorMessage("Le nombre de places est obligatoire");
            valid = false;
        }

        if (!valid) {
            NotificationHelper.showWarning("Veuillez corriger les erreurs dans le formulaire");
        }

        return valid;
    }
}
