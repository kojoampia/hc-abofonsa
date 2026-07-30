import {
  entityConfirmDeleteButtonSelector,
  entityCreateButtonSelector,
  entityCreateCancelButtonSelector,
  entityCreateSaveButtonSelector,
  entityDeleteButtonSelector,
  entityDetailsBackButtonSelector,
  entityDetailsButtonSelector,
  entityEditButtonSelector,
  entityTableSelector,
} from '../../support/entity';

describe('LaunchSetting e2e test', () => {
  const launchSettingPageUrl = '/launch-setting';
  const launchSettingPageUrlPattern = new RegExp('/launch-setting(\\?.*)?$');
  let username: string;
  let password: string;
  const launchSettingSample = {
    settingKey: 'anenst content',
    organisationName: 'instructor vaguely unlike',
    launchAt: '2026-07-29T23:28:47.194Z',
    launchTimezone: 'amidst',
    fundUrl: 'than',
    contactEmail: 'lest verify black-and-white',
    active: true,
  };

  let launchSetting;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/launch-settings+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/launch-settings').as('postEntityRequest');
    cy.intercept('DELETE', '/api/launch-settings/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (launchSetting) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/launch-settings/${launchSetting.id}`,
      }).then(() => {
        launchSetting = undefined;
      });
    }
  });

  it('LaunchSettings menu should load LaunchSettings page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('launch-setting');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('LaunchSetting').should('exist');
    cy.url().should('match', launchSettingPageUrlPattern);
  });

  describe('LaunchSetting page', () => {
    it('should have translated page title', () => {
      cy.visit(launchSettingPageUrl);
      cy.getEntityHeading('LaunchSetting').should('not.contain', 'abofonsaPreviewApp.launchSetting.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(launchSettingPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create LaunchSetting page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/launch-setting/new$'));
        cy.getEntityCreateUpdateHeading('LaunchSetting');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', launchSettingPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/launch-settings',
          body: launchSettingSample,
        }).then(({ body }) => {
          launchSetting = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/launch-settings+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [launchSetting],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(launchSettingPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details LaunchSetting page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('launchSetting');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', launchSettingPageUrlPattern);
      });

      it('edit button click should load edit LaunchSetting page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('LaunchSetting');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', launchSettingPageUrlPattern);
      });

      it('edit button click should load edit LaunchSetting page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('LaunchSetting');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', launchSettingPageUrlPattern);
      });

      it('last delete button click should delete instance of LaunchSetting', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('launchSetting').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', launchSettingPageUrlPattern);

        launchSetting = undefined;
      });
    });
  });

  describe('new LaunchSetting page', () => {
    beforeEach(() => {
      cy.visit(launchSettingPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('LaunchSetting');
    });

    it('should create an instance of LaunchSetting', () => {
      cy.get(`[data-cy="settingKey"]`).type('versus boastfully');
      cy.get(`[data-cy="settingKey"]`).should('have.value', 'versus boastfully');

      cy.get(`[data-cy="organisationName"]`).type('replicate behind');
      cy.get(`[data-cy="organisationName"]`).should('have.value', 'replicate behind');

      cy.get(`[data-cy="tagline"]`).type('duh eek gee');
      cy.get(`[data-cy="tagline"]`).should('have.value', 'duh eek gee');

      cy.get(`[data-cy="launchAt"]`).type('2026-07-30T09:14');
      cy.get(`[data-cy="launchAt"]`).blur();
      cy.get(`[data-cy="launchAt"]`).should('have.value', '2026-07-30T09:14');

      cy.get(`[data-cy="launchTimezone"]`).type('circa chunder');
      cy.get(`[data-cy="launchTimezone"]`).should('have.value', 'circa chunder');

      cy.get(`[data-cy="fundUrl"]`).type('mortise');
      cy.get(`[data-cy="fundUrl"]`).should('have.value', 'mortise');

      cy.get(`[data-cy="contactEmail"]`).type('to yahoo stranger');
      cy.get(`[data-cy="contactEmail"]`).should('have.value', 'to yahoo stranger');

      cy.get(`[data-cy="contactPhone"]`).type('elegantly defensive');
      cy.get(`[data-cy="contactPhone"]`).should('have.value', 'elegantly defensive');

      cy.get(`[data-cy="officeAddress"]`).type('spectate deselect shakily');
      cy.get(`[data-cy="officeAddress"]`).should('have.value', 'spectate deselect shakily');

      cy.get(`[data-cy="active"]`).should('not.be.checked');
      cy.get(`[data-cy="active"]`).click();
      cy.get(`[data-cy="active"]`).should('be.checked');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        launchSetting = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', launchSettingPageUrlPattern);
    });
  });
});
