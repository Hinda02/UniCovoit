package com.unicovoit.views.home;

import com.unicovoit.util.SessionManager;
import com.unicovoit.views.layout.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Accueil | UniCovoit")
public class HomeView extends VerticalLayout {

    public HomeView() {
        if (!SessionManager.isLoggedIn()) {
            getUI().ifPresent(ui -> ui.navigate("login"));
            return;
        }

        setSizeFull();
        setPadding(false);
        setSpacing(false);

        add(createHeroSection(), createFeaturesSection(), createCtaSection());
    }

    private Div createHeroSection() {
        Div hero = new Div();
        hero.addClassNames(
                LumoUtility.Background.CONTRAST_5,
                LumoUtility.Padding.XLARGE,
                LumoUtility.BorderRadius.MEDIUM
        );
        hero.getStyle().set("margin", "var(--lumo-space-l)");

        H1 title = new H1("Bienvenue sur UniCovoit");
        title.addClassNames(LumoUtility.TextColor.PRIMARY, LumoUtility.Margin.Bottom.MEDIUM);

        Paragraph subtitle = new Paragraph(
                "La plateforme de covoiturage dédiée aux étudiants. " +
                "Partagez vos trajets, économisez de l'argent et réduisez votre empreinte carbone !"
        );
        subtitle.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.LARGE);

        HorizontalLayout actions = new HorizontalLayout();
        actions.addClassName(LumoUtility.Gap.MEDIUM);

        Button searchButton = new Button("Rechercher un trajet", VaadinIcon.SEARCH.create());
        searchButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        searchButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("search")));

        Button createRideButton = new Button("Proposer un trajet", VaadinIcon.CAR.create());
        createRideButton.addThemeVariants(ButtonVariant.LUMO_LARGE);
        createRideButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("create-ride")));

        actions.add(searchButton, createRideButton);

        hero.add(title, subtitle, actions);
        return hero;
    }

    private HorizontalLayout createFeaturesSection() {
        HorizontalLayout features = new HorizontalLayout();
        features.setWidthFull();
        features.addClassName(LumoUtility.Padding.LARGE);
        features.addClassName(LumoUtility.Gap.LARGE);

        features.add(
                createFeatureCard(
                        VaadinIcon.MONEY,
                        "Économique",
                        "Partagez les frais de transport et économisez sur vos déplacements quotidiens"
                ),
                createFeatureCard(
                        VaadinIcon.GROUP,
                        "Communautaire",
                        "Rencontrez d'autres étudiants et créez des liens pendant vos trajets"
                ),
                createFeatureCard(
                        VaadinIcon.GLOBE,
                        "Écologique",
                        "Réduisez votre empreinte carbone en partageant votre véhicule"
                )
        );

        return features;
    }

    private Div createFeatureCard(VaadinIcon icon, String title, String description) {
        Div card = new Div();
        card.addClassNames(
                LumoUtility.Background.BASE,
                LumoUtility.BoxShadow.MEDIUM,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.Padding.LARGE
        );
        card.getStyle()
                .set("flex", "1")
                .set("text-align", "center");

        Div iconDiv = new Div();
        iconDiv.addClassNames(LumoUtility.TextColor.PRIMARY, LumoUtility.Margin.Bottom.MEDIUM);
        iconDiv.getStyle()
                .set("font-size", "3rem");
        iconDiv.add(icon.create());

        H3 cardTitle = new H3(title);
        cardTitle.addClassName(LumoUtility.Margin.Bottom.SMALL);

        Paragraph cardDesc = new Paragraph(description);
        cardDesc.addClassName(LumoUtility.TextColor.SECONDARY);

        card.add(iconDiv, cardTitle, cardDesc);
        return card;
    }

    private Div createCtaSection() {
        Div cta = new Div();
        cta.addClassNames(
                LumoUtility.Background.PRIMARY,
                LumoUtility.TextColor.PRIMARY_CONTRAST,
                LumoUtility.Padding.XLARGE,
                LumoUtility.TextAlignment.CENTER
        );
        cta.getStyle().set("margin", "var(--lumo-space-l)");

        H2 title = new H2("Prêt à commencer ?");
        title.addClassName(LumoUtility.Margin.Bottom.MEDIUM);

        Paragraph text = new Paragraph("Ajoutez votre véhicule et commencez à proposer des trajets dès maintenant !");
        text.addClassName(LumoUtility.Margin.Bottom.LARGE);

        Button button = new Button("Gérer mes véhicules", VaadinIcon.AUTOMOBILE.create());
        button.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_LARGE);
        button.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("vehicles")));

        cta.add(title, text, button);
        return cta;
    }
}
