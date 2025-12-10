package com.unicovoit.views.home;

import com.vaadin.flow.theme.lumo.LumoUtility;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.html.Anchor;
import com.unicovoit.views.auth.LoginView;


@Route("")                     // this is the home page: http://localhost:8080
@PageTitle("UniCovoit")
public class HomeView extends VerticalLayout {

    public HomeView() {
        setSizeFull();
        setWidthFull();                              
        setPadding(false);                             
        setSpacing(false);                               
        setMargin(false);                                
        setDefaultHorizontalComponentAlignment(Alignment.STRETCH); 
        
        addClassName("page-root");

        // HEADER
        add(createHeader());

        // HERO SECTION (blue background with search card)
        add(createHeroSection());

        // FEATURES SECTION (3 icons/text blocks)
        add(createFeaturesSection());

        // CTA SECTION (ready to start)
        add(createCtaSection());

        // FOOTER
        add(createFooter());
    }

    private Component createHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.addClassName("header");
        header.setWidthFull();
        header.setPadding(true);
        header.setSpacing(true);
        header.setAlignItems(Alignment.CENTER);
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);

        // Logo + name
        HorizontalLayout logoArea = new HorizontalLayout();
        logoArea.addClassName("logo-area");
        Image logo = new Image("icons/unicovoit-logo.svg", "UniCovoit"); // you can change to any image or remove
        logo.addClassName("logo-icon");
        H3 logoText = new H3("UniCovoit");
        logoText.addClassName("logo-text");
        logoArea.add(logo, logoText);
        logoArea.setAlignItems(Alignment.CENTER);

        // Right buttons
        Anchor signupLink = new Anchor("register", "S'inscrire");
        signupLink.addClassName("link-signup");

        // Button for login, using programmatic navigation
        Button loginBtn = new Button("Se connecter", e ->
        e.getSource().getUI().ifPresent(ui -> ui.navigate("login"))
		);
		loginBtn.addClassName("btn-login");

        HorizontalLayout right = new HorizontalLayout(signupLink, loginBtn);
        right.addClassName("header-right");
        right.setAlignItems(Alignment.CENTER);

        header.add(logoArea, right);
        return header;
    }

    private Component createHeroSection() {
        Div hero = new Div();
        hero.addClassName("hero");

        // Text
        Paragraph subtitle = new Paragraph("Trouvez un trajet ou proposez un trajet");
        subtitle.addClassName("hero-subtitle");

        H2 title = new H2(
                "Partagez vos trajets entre étudiants, économisez et réduisez votre empreinte carbone");
        title.addClassName("hero-title");

        // Search card
        Div searchCard = new Div();
        searchCard.addClassName("search-card");

        TextField departureField = new TextField("Départ");
        departureField.setPlaceholder("Ville de départ");
        departureField.addClassName("search-field");

        TextField arrivalField = new TextField("Arrivée");
        arrivalField.setPlaceholder("Ville d'arrivée");
        arrivalField.addClassName("search-field");

        DatePicker dateField = new DatePicker("Date");
        dateField.setPlaceholder("jj/mm/aaaa");
        dateField.addClassName("search-field");

        HorizontalLayout fieldsRow = new HorizontalLayout(departureField, arrivalField, dateField);
        fieldsRow.addClassName("search-row");
        fieldsRow.setWidthFull();

        Button searchBtn = new Button("Rechercher");
        searchBtn.addClassName("btn-search");
        searchBtn.addClickListener(e ->
        e.getSource().getUI().ifPresent(ui -> ui.navigate("results"))
);


        HorizontalLayout btnRow = new HorizontalLayout(searchBtn);
        btnRow.addClassName("search-btn-row");
        btnRow.setWidthFull();
        btnRow.setJustifyContentMode(JustifyContentMode.START);

        searchCard.add(fieldsRow, btnRow);

        hero.add(subtitle, title, searchCard);
        return hero;
    }

    private Component createFeaturesSection() {
        Div section = new Div();
        section.addClassName("features-section");

        Paragraph title = new Paragraph("Pourquoi choisir UniCovoit ?");
        title.addClassName("features-title");

        // 3 feature blocks
        VerticalLayout f1 = featureBlock(
                "Communauté étudiante",
                "Voyagez uniquement avec des étudiants vérifiés de votre université");
        VerticalLayout f2 = featureBlock(
                "Éco-responsable",
                "Réduisez votre empreinte carbone en partageant vos trajets");
        VerticalLayout f3 = featureBlock(
                "Sécurisé et fiable",
                "Profils vérifiés, évaluations et système de paiement sécurisé");

        HorizontalLayout cards = new HorizontalLayout(f1, f2, f3);
        cards.addClassName("features-cards");
        cards.setWidthFull();
        cards.setJustifyContentMode(JustifyContentMode.CENTER);
cards.addClassNames(LumoUtility.Gap.Column.LARGE);

        section.add(title, cards);
        return section;
    }

    private VerticalLayout featureBlock(String title, String text) {
        VerticalLayout card = new VerticalLayout();
        card.addClassName("feature-card");
        card.setSpacing(false);
        card.setAlignItems(Alignment.CENTER);

        Div iconCircle = new Div();
        iconCircle.addClassName("feature-icon");  // purely decorative circle

        H4 heading = new H4(title);
        heading.addClassName("feature-heading");

        Paragraph p = new Paragraph(text);
        p.addClassName("feature-text");

        card.add(iconCircle, heading, p);
        return card;
    }

    private Component createCtaSection() {
        Div cta = new Div();
        cta.addClassName("cta-section");

        Paragraph ready = new Paragraph("Prêt à commencer ?");
        ready.addClassName("cta-title");

        Paragraph subtitle = new Paragraph("Rejoignez des milliers d'étudiants qui covoiturent déjà");
        subtitle.addClassName("cta-subtitle");

        Button btn = new Button("S'inscrire gratuitement");
        btn.addClassName("btn-cta");

        cta.add(ready, subtitle, btn);
        return cta;
    }

    private Component createFooter() {
        Div footer = new Div();
        footer.addClassName("footer");
        footer.setWidthFull();

        Paragraph p = new Paragraph("© 2025 UniCovoit – Plateforme de covoiturage étudiante");
        p.addClassName("footer-text");

        footer.add(p);
        return footer;
    }
}
