package com.unicovoit.views.layout;

import com.unicovoit.entity.UserAccount;
import com.unicovoit.util.SessionManager;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.lumo.LumoUtility;

/**
 * Main layout for authenticated pages
 */
@PageTitle("UniCovoit")
public class MainLayout extends AppLayout {

    private H2 viewTitle;

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        // User info and logout button
        UserAccount currentUser = SessionManager.getCurrentUser();
        String userInitials = "";
        String userName = "";

        if (currentUser != null) {
            userInitials = currentUser.getFirstName().substring(0, 1).toUpperCase() +
                          currentUser.getLastName().substring(0, 1).toUpperCase();
            userName = currentUser.getFirstName() + " " + currentUser.getLastName();
        }

        Avatar avatar = new Avatar(userName);
        avatar.setAbbreviation(userInitials);
        avatar.addClassNames(LumoUtility.Margin.SMALL);

        Span userNameSpan = new Span(userName);
        userNameSpan.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);

        Button logoutButton = new Button("Déconnexion", new Icon(VaadinIcon.SIGN_OUT));
        logoutButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        logoutButton.addClickListener(e -> {
            SessionManager.logout();
            getUI().ifPresent(ui -> ui.navigate("login"));
        });

        HorizontalLayout userLayout = new HorizontalLayout(avatar, userNameSpan, logoutButton);
        userLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        userLayout.addClassNames(LumoUtility.Gap.MEDIUM);

        HorizontalLayout header = new HorizontalLayout(toggle, viewTitle);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(viewTitle);
        header.setWidthFull();
        header.addClassNames(
                LumoUtility.Padding.Vertical.NONE,
                LumoUtility.Padding.Horizontal.MEDIUM
        );

        HorizontalLayout fullHeader = new HorizontalLayout(header, userLayout);
        fullHeader.setWidthFull();
        fullHeader.expand(header);
        fullHeader.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        fullHeader.addClassNames(
                LumoUtility.Background.BASE,
                LumoUtility.BoxShadow.SMALL
        );

        addToNavbar(true, fullHeader);
    }

    private void addDrawerContent() {
        H2 appName = new H2("UniCovoit");
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        Header header = new Header(appName);
        header.addClassNames(
                LumoUtility.Background.PRIMARY,
                LumoUtility.TextColor.PRIMARY_CONTRAST,
                LumoUtility.Padding.MEDIUM
        );

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();

        nav.addItem(new SideNavItem("Accueil", "", VaadinIcon.HOME.create()));
        nav.addItem(new SideNavItem("Rechercher un trajet", "search", VaadinIcon.SEARCH.create()));
        nav.addItem(new SideNavItem("Proposer un trajet", "create-ride", VaadinIcon.CAR.create()));
        nav.addItem(new SideNavItem("Mes trajets", "my-rides", VaadinIcon.ROAD.create()));
        nav.addItem(new SideNavItem("Mes réservations", "my-bookings", VaadinIcon.TICKET.create()));
        nav.addItem(new SideNavItem("Demandes de réservation", "booking-requests", VaadinIcon.CLIPBOARD_CHECK.create()));
        nav.addItem(new SideNavItem("Mes véhicules", "vehicles", VaadinIcon.AUTOMOBILE.create()));
        nav.addItem(new SideNavItem("Messages", "messages", VaadinIcon.ENVELOPE.create()));

        return nav;
    }

    private Footer createFooter() {
        Footer footer = new Footer();
        footer.addClassNames(
                LumoUtility.Padding.MEDIUM,
                LumoUtility.TextColor.SECONDARY,
                LumoUtility.FontSize.XSMALL,
                LumoUtility.TextAlignment.CENTER
        );

        Span text = new Span("© 2025 UniCovoit - Covoiturage Universitaire");
        footer.add(text);

        return footer;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
