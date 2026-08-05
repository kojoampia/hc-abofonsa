import { Routes } from '@angular/router';

import passwordRoute from './password/password.route';
import settingsRoute from './settings/settings.route';

/**
 * What the signed-in operator can do to their own account, and nothing else.
 *
 * <p>`register`, `activate`, `password-reset/init` and `password-reset/finish` are gone, along with
 * their components. Their endpoints are deleted from `AccountResource` and denied in
 * `SecurityConfiguration`: this application has exactly one account, so there is no sign-up to open.
 * The pages were still being shipped and routable, which meant the bundle advertised a flow the
 * server would refuse.
 */
const accountRoutes: Routes = [passwordRoute, settingsRoute];

export default accountRoutes;
