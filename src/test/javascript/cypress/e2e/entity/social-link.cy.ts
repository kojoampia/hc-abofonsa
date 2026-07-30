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

describe('SocialLink e2e test', () => {
  const socialLinkPageUrl = '/social-link';
  const socialLinkPageUrlPattern = new RegExp('/social-link(\\?.*)?$');
  let username: string;
  let password: string;
  const socialLinkSample = {
    platform: 'LINKEDIN',
    label: 'strictly',
    url: 'https://circular-technician.info/',
    displayOrder: 25277,
    active: true,
  };

  let socialLink;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/social-links+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/social-links').as('postEntityRequest');
    cy.intercept('DELETE', '/api/social-links/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (socialLink) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/social-links/${socialLink.id}`,
      }).then(() => {
        socialLink = undefined;
      });
    }
  });

  it('SocialLinks menu should load SocialLinks page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('social-link');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('SocialLink').should('exist');
    cy.url().should('match', socialLinkPageUrlPattern);
  });

  describe('SocialLink page', () => {
    it('should have translated page title', () => {
      cy.visit(socialLinkPageUrl);
      cy.getEntityHeading('SocialLink').should('not.contain', 'abofonsaPreviewApp.socialLink.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(socialLinkPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create SocialLink page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/social-link/new$'));
        cy.getEntityCreateUpdateHeading('SocialLink');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', socialLinkPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/social-links',
          body: socialLinkSample,
        }).then(({ body }) => {
          socialLink = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/social-links+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [socialLink],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(socialLinkPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details SocialLink page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('socialLink');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', socialLinkPageUrlPattern);
      });

      it('edit button click should load edit SocialLink page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('SocialLink');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', socialLinkPageUrlPattern);
      });

      it('edit button click should load edit SocialLink page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('SocialLink');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', socialLinkPageUrlPattern);
      });

      it('last delete button click should delete instance of SocialLink', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('socialLink').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', socialLinkPageUrlPattern);

        socialLink = undefined;
      });
    });
  });

  describe('new SocialLink page', () => {
    beforeEach(() => {
      cy.visit(socialLinkPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('SocialLink');
    });

    it('should create an instance of SocialLink', () => {
      cy.get(`[data-cy="platform"]`).select('PHONE');

      cy.get(`[data-cy="label"]`).type('once');
      cy.get(`[data-cy="label"]`).should('have.value', 'once');

      cy.get(`[data-cy="url"]`).type('https://near-translation.name');
      cy.get(`[data-cy="url"]`).should('have.value', 'https://near-translation.name');

      cy.get(`[data-cy="iconKey"]`).type('notwithstanding reword lively');
      cy.get(`[data-cy="iconKey"]`).should('have.value', 'notwithstanding reword lively');

      cy.get(`[data-cy="displayOrder"]`).type('18805');
      cy.get(`[data-cy="displayOrder"]`).should('have.value', '18805');

      cy.get(`[data-cy="active"]`).should('not.be.checked');
      cy.get(`[data-cy="active"]`).click();
      cy.get(`[data-cy="active"]`).should('be.checked');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        socialLink = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', socialLinkPageUrlPattern);
    });
  });
});
