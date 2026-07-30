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

describe('WaitlistSignup e2e test', () => {
  const waitlistSignupPageUrl = '/waitlist-signup';
  const waitlistSignupPageUrlPattern = new RegExp('/waitlist-signup(\\?.*)?$');
  let username: string;
  let password: string;
  const waitlistSignupSample = {
    email: 'y@bq!.ek!|',
    emailNormalized: 'above',
    status: 'BOUNCED',
    consentGiven: false,
    capturedAt: '2026-07-29T23:38:29.486Z',
  };

  let waitlistSignup;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/waitlist-signups+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/waitlist-signups').as('postEntityRequest');
    cy.intercept('DELETE', '/api/waitlist-signups/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (waitlistSignup) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/waitlist-signups/${waitlistSignup.id}`,
      }).then(() => {
        waitlistSignup = undefined;
      });
    }
  });

  it('WaitlistSignups menu should load WaitlistSignups page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('waitlist-signup');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('WaitlistSignup').should('exist');
    cy.url().should('match', waitlistSignupPageUrlPattern);
  });

  describe('WaitlistSignup page', () => {
    it('should have translated page title', () => {
      cy.visit(waitlistSignupPageUrl);
      cy.getEntityHeading('WaitlistSignup').should('not.contain', 'abofonsaPreviewApp.waitlistSignup.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(waitlistSignupPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create WaitlistSignup page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/waitlist-signup/new$'));
        cy.getEntityCreateUpdateHeading('WaitlistSignup');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', waitlistSignupPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/waitlist-signups',
          body: waitlistSignupSample,
        }).then(({ body }) => {
          waitlistSignup = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/waitlist-signups+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/waitlist-signups?page=0&size=20>; rel="last",<http://localhost/api/waitlist-signups?page=0&size=20>; rel="first"',
              },
              body: [waitlistSignup],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(waitlistSignupPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details WaitlistSignup page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('waitlistSignup');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', waitlistSignupPageUrlPattern);
      });

      it('edit button click should load edit WaitlistSignup page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('WaitlistSignup');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', waitlistSignupPageUrlPattern);
      });

      it('edit button click should load edit WaitlistSignup page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('WaitlistSignup');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', waitlistSignupPageUrlPattern);
      });

      it('last delete button click should delete instance of WaitlistSignup', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('waitlistSignup').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', waitlistSignupPageUrlPattern);

        waitlistSignup = undefined;
      });
    });
  });

  describe('new WaitlistSignup page', () => {
    beforeEach(() => {
      cy.visit(waitlistSignupPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('WaitlistSignup');
    });

    it('should create an instance of WaitlistSignup', () => {
      cy.get(`[data-cy="email"]`).type('i@3.%');
      cy.get(`[data-cy="email"]`).should('have.value', 'i@3.%');

      cy.get(`[data-cy="emailNormalized"]`).type('jovially meanwhile');
      cy.get(`[data-cy="emailNormalized"]`).should('have.value', 'jovially meanwhile');

      cy.get(`[data-cy="fullName"]`).type('pulverize pointed irritably');
      cy.get(`[data-cy="fullName"]`).should('have.value', 'pulverize pointed irritably');

      cy.get(`[data-cy="organisation"]`).type('clavicle shyly foolishly');
      cy.get(`[data-cy="organisation"]`).should('have.value', 'clavicle shyly foolishly');

      cy.get(`[data-cy="audience"]`).select('CAREGIVER');

      cy.get(`[data-cy="planOfInterest"]`).select('MELON');

      cy.get(`[data-cy="status"]`).select('PENDING');

      cy.get(`[data-cy="locale"]`).type('ragged blo');
      cy.get(`[data-cy="locale"]`).should('have.value', 'ragged blo');

      cy.get(`[data-cy="sourcePage"]`).type('circle');
      cy.get(`[data-cy="sourcePage"]`).should('have.value', 'circle');

      cy.get(`[data-cy="utmSource"]`).type('with buttery whereas');
      cy.get(`[data-cy="utmSource"]`).should('have.value', 'with buttery whereas');

      cy.get(`[data-cy="utmMedium"]`).type('discontinue gratefully');
      cy.get(`[data-cy="utmMedium"]`).should('have.value', 'discontinue gratefully');

      cy.get(`[data-cy="utmCampaign"]`).type('emotional');
      cy.get(`[data-cy="utmCampaign"]`).should('have.value', 'emotional');

      cy.get(`[data-cy="referrer"]`).type('configuration sturdy');
      cy.get(`[data-cy="referrer"]`).should('have.value', 'configuration sturdy');

      cy.get(`[data-cy="deviceType"]`).select('BOT');

      cy.get(`[data-cy="consentGiven"]`).should('not.be.checked');
      cy.get(`[data-cy="consentGiven"]`).click();
      cy.get(`[data-cy="consentGiven"]`).should('be.checked');

      cy.get(`[data-cy="confirmationToken"]`).type('gently');
      cy.get(`[data-cy="confirmationToken"]`).should('have.value', 'gently');

      cy.get(`[data-cy="confirmedAt"]`).type('2026-07-30T01:25');
      cy.get(`[data-cy="confirmedAt"]`).blur();
      cy.get(`[data-cy="confirmedAt"]`).should('have.value', '2026-07-30T01:25');

      cy.get(`[data-cy="unsubscribedAt"]`).type('2026-07-30T00:04');
      cy.get(`[data-cy="unsubscribedAt"]`).blur();
      cy.get(`[data-cy="unsubscribedAt"]`).should('have.value', '2026-07-30T00:04');

      cy.get(`[data-cy="capturedAt"]`).type('2026-07-30T06:06');
      cy.get(`[data-cy="capturedAt"]`).blur();
      cy.get(`[data-cy="capturedAt"]`).should('have.value', '2026-07-30T06:06');

      cy.get(`[data-cy="ipHash"]`).type('drat trusty stake');
      cy.get(`[data-cy="ipHash"]`).should('have.value', 'drat trusty stake');

      cy.get(`[data-cy="userAgent"]`).type('scarily distant persecute');
      cy.get(`[data-cy="userAgent"]`).should('have.value', 'scarily distant persecute');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        waitlistSignup = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', waitlistSignupPageUrlPattern);
    });
  });
});
