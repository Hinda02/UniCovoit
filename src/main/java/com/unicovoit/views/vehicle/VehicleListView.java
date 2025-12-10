package com.unicovoit.views.vehicle;

import com.unicovoit.entity.Vehicle;
import com.unicovoit.service.VehicleService;
import com.unicovoit.util.NotificationHelper;
import com.unicovoit.util.SessionManager;
import com.unicovoit.views.layout.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.List;

@Route(value = "vehicles", layout = MainLayout.class)
@PageTitle("Mes véhicules | UniCovoit")
public class VehicleListView extends VerticalLayout {

    private final VehicleService vehicleService;
    private final Grid<Vehicle> grid = new Grid<>(Vehicle.class, false);

    public VehicleListView(VehicleService vehicleService) {
        this.vehicleService = vehicleService;

        if (!SessionManager.isLoggedIn()) {
            getUI().ifPresent(ui -> ui.navigate("login"));
            return;
        }

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        add(createHeader(), createGridSection());
        loadVehicles();
    }

    private HorizontalLayout createHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);
        header.setAlignItems(Alignment.CENTER);

        Div titleSection = new Div();
        H2 title = new H2("Mes véhicules");
        title.addClassName(LumoUtility.Margin.NONE);
        Paragraph subtitle = new Paragraph("Gérez vos véhicules pour proposer des trajets");
        subtitle.addClassName(LumoUtility.TextColor.SECONDARY);
        titleSection.add(title, subtitle);

        Button addButton = new Button("Ajouter un véhicule", VaadinIcon.PLUS.create());
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("vehicles/new")));

        header.add(titleSection, addButton);
        return header;
    }

    private VerticalLayout createGridSection() {
        VerticalLayout section = new VerticalLayout();
        section.setSizeFull();
        section.setPadding(false);

        configureGrid();
        section.add(grid);

        return section;
    }

    private void configureGrid() {
        grid.addClassName("vehicle-grid");
        grid.setSizeFull();

        grid.addColumn(Vehicle::getBrand)
                .setHeader("Marque")
                .setSortable(true)
                .setFlexGrow(1);

        grid.addColumn(Vehicle::getModel)
                .setHeader("Modèle")
                .setSortable(true)
                .setFlexGrow(1);

        grid.addColumn(Vehicle::getColor)
                .setHeader("Couleur")
                .setFlexGrow(1);

        grid.addColumn(Vehicle::getPlateNumber)
                .setHeader("Immatriculation")
                .setFlexGrow(1);

        grid.addColumn(Vehicle::getSeatsTotal)
                .setHeader("Places")
                .setFlexGrow(0)
                .setWidth("100px");

        grid.addComponentColumn(vehicle -> {
            HorizontalLayout actions = new HorizontalLayout();
            actions.setSpacing(true);

            Button editButton = new Button("Modifier", VaadinIcon.EDIT.create());
            editButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
            editButton.addClickListener(e -> editVehicle(vehicle));

            Button deleteButton = new Button("Supprimer", VaadinIcon.TRASH.create());
            deleteButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            deleteButton.addClickListener(e -> confirmDelete(vehicle));

            actions.add(editButton, deleteButton);
            return actions;
        }).setHeader("Actions").setFlexGrow(0).setWidth("200px");

        // Empty state
        grid.setEmptyStateText("Aucun véhicule enregistré");
    }

    private void loadVehicles() {
        try {
            Long userId = SessionManager.getCurrentUserId();
            List<Vehicle> vehicles = vehicleService.getUserVehicles(userId);
            grid.setItems(vehicles);

            if (vehicles.isEmpty()) {
                showEmptyState();
            }
        } catch (Exception ex) {
            NotificationHelper.showError("Erreur lors du chargement des véhicules: " + ex.getMessage());
        }
    }

    private void showEmptyState() {
        // Grid already has empty state text
    }

    private void editVehicle(Vehicle vehicle) {
        getUI().ifPresent(ui -> ui.navigate("vehicles/edit/" + vehicle.getId()));
    }

    private void confirmDelete(Vehicle vehicle) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Supprimer le véhicule");
        dialog.setText(String.format(
                "Êtes-vous sûr de vouloir supprimer le véhicule %s %s ?",
                vehicle.getBrand(), vehicle.getModel()
        ));

        dialog.setCancelable(true);
        dialog.setCancelText("Annuler");

        dialog.setConfirmText("Supprimer");
        dialog.setConfirmButtonTheme("error primary");

        dialog.addConfirmListener(e -> deleteVehicle(vehicle));

        dialog.open();
    }

    private void deleteVehicle(Vehicle vehicle) {
        try {
            vehicleService.deleteVehicle(vehicle.getId(), SessionManager.getCurrentUser());
            NotificationHelper.showSuccess("Véhicule supprimé avec succès");
            loadVehicles();
        } catch (Exception ex) {
            NotificationHelper.showError("Erreur lors de la suppression: " + ex.getMessage());
        }
    }
}
