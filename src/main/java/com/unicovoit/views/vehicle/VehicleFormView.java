package com.unicovoit.views.vehicle;

import com.unicovoit.dto.VehicleDto;
import com.unicovoit.entity.Vehicle;
import com.unicovoit.service.VehicleService;
import com.unicovoit.util.NotificationHelper;
import com.unicovoit.util.SessionManager;
import com.unicovoit.views.layout.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.Optional;

@Route(value = "vehicles/:action?/:id?", layout = MainLayout.class)
@PageTitle("Véhicule | UniCovoit")
public class VehicleFormView extends VerticalLayout implements BeforeEnterObserver {

    private final VehicleService vehicleService;

    private final TextField brandField = new TextField("Marque");
    private final TextField modelField = new TextField("Modèle");
    private final TextField colorField = new TextField("Couleur");
    private final TextField plateNumberField = new TextField("Immatriculation");
    private final IntegerField seatsField = new IntegerField("Nombre de places");

    private final Button saveButton = new Button("Enregistrer");
    private final Button cancelButton = new Button("Annuler");

    private Long vehicleId;
    private boolean isEditMode = false;

    public VehicleFormView(VehicleService vehicleService) {
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
    }

    private Div createFormCard() {
        Div card = new Div();
        card.addClassNames(LumoUtility.Background.BASE, LumoUtility.BoxShadow.LARGE, LumoUtility.BorderRadius.LARGE);
        card.setWidth("600px");
        card.getStyle().set("padding", "var(--lumo-space-xl)");

        H2 title = new H2(isEditMode ? "Modifier le véhicule" : "Ajouter un véhicule");
        title.addClassName(LumoUtility.Margin.Top.NONE);

        Paragraph subtitle = new Paragraph("Renseignez les informations de votre véhicule");
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
                new FormLayout.ResponsiveStep("500px", 2)
        );

        // Brand field
        brandField.setPlaceholder("Ex: Peugeot");
        brandField.setPrefixComponent(VaadinIcon.AUTOMOBILE.create());
        brandField.setRequired(true);
        brandField.setMaxLength(100);

        // Model field
        modelField.setPlaceholder("Ex: 208");
        modelField.setPrefixComponent(VaadinIcon.CAR.create());
        modelField.setRequired(true);
        modelField.setMaxLength(100);

        // Color field
        colorField.setPlaceholder("Ex: Bleu");
        colorField.setPrefixComponent(VaadinIcon.PAINTBRUSH.create());
        colorField.setMaxLength(50);

        // Plate number field
        plateNumberField.setPlaceholder("Ex: AB-123-CD");
        plateNumberField.setPrefixComponent(VaadinIcon.BARCODE.create());
        plateNumberField.setMaxLength(50);

        // Seats field
        seatsField.setPlaceholder("Ex: 4");
        seatsField.setPrefixComponent(VaadinIcon.GROUP.create());
        seatsField.setRequired(true);
        seatsField.setMin(1);
        seatsField.setMax(8);
        seatsField.setStepButtonsVisible(true);
        seatsField.setValue(4);
        seatsField.setHelperText("Entre 1 et 8 places");

        form.add(brandField, modelField);
        form.add(colorField, plateNumberField);
        form.add(seatsField, 1);

        return form;
    }

    private HorizontalLayout createActions() {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setWidthFull();
        actions.setJustifyContentMode(JustifyContentMode.END);
        actions.addClassName(LumoUtility.Gap.MEDIUM);

        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.setIcon(VaadinIcon.CLOSE.create());
        cancelButton.addClickListener(e -> navigateToList());

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.setIcon(VaadinIcon.CHECK.create());
        saveButton.addClickListener(e -> saveVehicle());

        actions.add(cancelButton, saveButton);
        return actions;
    }

    private void saveVehicle() {
        if (!validateForm()) {
            return;
        }

        saveButton.setEnabled(false);
        saveButton.setText("Enregistrement...");

        try {
            VehicleDto dto = new VehicleDto();
            dto.setBrand(brandField.getValue().trim());
            dto.setModel(modelField.getValue().trim());
            dto.setColor(colorField.getValue() != null ? colorField.getValue().trim() : null);
            dto.setPlateNumber(plateNumberField.getValue() != null ? plateNumberField.getValue().trim() : null);
            dto.setSeatsTotal(seatsField.getValue());

            if (isEditMode && vehicleId != null) {
                vehicleService.updateVehicle(vehicleId, dto, SessionManager.getCurrentUser());
                NotificationHelper.showSuccess("Véhicule modifié avec succès");
            } else {
                vehicleService.createVehicle(dto, SessionManager.getCurrentUser());
                NotificationHelper.showSuccess("Véhicule ajouté avec succès");
            }

            navigateToList();

        } catch (Exception ex) {
            NotificationHelper.showError(ex.getMessage());
            saveButton.setEnabled(true);
            saveButton.setText("Enregistrer");
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        if (brandField.isEmpty()) {
            brandField.setInvalid(true);
            brandField.setErrorMessage("La marque est obligatoire");
            valid = false;
        } else {
            brandField.setInvalid(false);
        }

        if (modelField.isEmpty()) {
            modelField.setInvalid(true);
            modelField.setErrorMessage("Le modèle est obligatoire");
            valid = false;
        } else {
            modelField.setInvalid(false);
        }

        if (seatsField.isEmpty() || seatsField.getValue() < 1 || seatsField.getValue() > 8) {
            seatsField.setInvalid(true);
            seatsField.setErrorMessage("Le nombre de places doit être entre 1 et 8");
            valid = false;
        } else {
            seatsField.setInvalid(false);
        }

        if (!valid) {
            NotificationHelper.showWarning("Veuillez corriger les erreurs dans le formulaire");
        }

        return valid;
    }

    private void loadVehicle(Long id) {
        try {
            Vehicle vehicle = vehicleService.getVehicleByIdAndOwner(id, SessionManager.getCurrentUser());

            brandField.setValue(vehicle.getBrand());
            modelField.setValue(vehicle.getModel());
            if (vehicle.getColor() != null) {
                colorField.setValue(vehicle.getColor());
            }
            if (vehicle.getPlateNumber() != null) {
                plateNumberField.setValue(vehicle.getPlateNumber());
            }
            seatsField.setValue(vehicle.getSeatsTotal());

        } catch (Exception ex) {
            NotificationHelper.showError("Erreur lors du chargement du véhicule: " + ex.getMessage());
            navigateToList();
        }
    }

    private void navigateToList() {
        getUI().ifPresent(ui -> ui.navigate("vehicles"));
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!SessionManager.isLoggedIn()) {
            event.forwardTo("login");
            return;
        }

        Optional<String> action = event.getRouteParameters().get("action");
        Optional<String> id = event.getRouteParameters().get("id");

        if (action.isPresent() && action.get().equals("edit") && id.isPresent()) {
            isEditMode = true;
            vehicleId = Long.parseLong(id.get());
            loadVehicle(vehicleId);
        }
    }
}
