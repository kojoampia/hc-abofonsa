package net.jojoaddison.abofonsa.preview.web.rest;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.servlet.support.ExtendedServletRequestDataBinder;

/**
 * Stops request headers being bound into controller model attributes.
 *
 * <p>Spring binds request <em>headers</em> into {@code @ModelAttribute} objects as well as query
 * parameters, converting {@code User-Agent} to a {@code userAgent} property. JHipster's generated
 * criteria classes are model attributes, and {@code WaitlistSignupCriteria} has a {@code userAgent}
 * field of type {@code StringFilter} — so every browser request carrying a User-Agent tried to bind
 * that header into a filter object, failed to convert, and returned 400.
 *
 * <p>The symptom was total: {@code GET /api/waitlist-signups} answered 400 for every caller, with a
 * message about type conversion that names no header and reads like a client mistake. curl without
 * headers would have worked, which is a good way to spend an afternoon.
 *
 * <p>Disabling header binding outright rather than renaming the field. The collision is not special
 * to this entity — {@code referrer}, {@code from} and {@code host} are all plausible column names —
 * and a filter should be something the caller asked for in the query string, never something their
 * browser volunteered.
 *
 * <p>Note the import: in Spring 7 this class lives in {@code web.servlet.support}, and the old
 * {@code web.servlet.mvc.method.annotation} name is a deprecated <em>subclass</em>. The binder
 * Spring actually hands over is the new type, so an {@code instanceof} against the old name is
 * always false — the advice runs, matches nothing, and the 400 stays exactly as it was.
 */
@ControllerAdvice
public class CriteriaBinderAdvice {

    @InitBinder
    public void disableHeaderBinding(WebDataBinder binder) {
        if (binder instanceof ExtendedServletRequestDataBinder extendedBinder) {
            extendedBinder.setHeaderPredicate(header -> false);
        }
    }
}
