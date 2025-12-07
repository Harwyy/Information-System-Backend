package is.is_backend.constraint;

import is.is_backend.exception.MyException;
import is.is_backend.models.Location;
import is.is_backend.models.Organization;
import is.is_backend.models.enums.OrganizationType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class OrganizationGeoBusinessConstraint {

    private static final double EARTH_RADIUS_KM = 6371.0;
    private static final double MAX_GOV_DISTANCE_KM = 10.0;
    private static final double MAX_TRUST_DISTANCE_KM = 1000.0;

    private static final double MIN_LATITUDE = -90.0;
    private static final double MAX_LATITUDE = 90.0;
    private static final double MIN_LONGITUDE = -180.0;
    private static final double MAX_LONGITUDE = 180.0;

    public void validateGovernmentConstraints(Organization org) {
        if (org.getPostalAddress().getTown() == null || org.getOfficialAddress().getTown() == null) {
            throw new MyException("Town should be provided for a government organization", HttpStatus.CONFLICT);
        }
        Location officialLocation = org.getOfficialAddress().getTown();
        Location postalLocation = org.getPostalAddress().getTown();
        validateLatLonConstraints(
                officialLocation.getX(), officialLocation.getY(), postalLocation.getX(), postalLocation.getY());
        double distance = calculateDistance(
                officialLocation.getX(), officialLocation.getY(),
                postalLocation.getX(), postalLocation.getY());
        if (org.getType().equals(OrganizationType.GOVERNMENT)) {
            if (distance > MAX_GOV_DISTANCE_KM) {
                throw new MyException(
                        String.format(
                                "For government organizations, official and postal addresses "
                                        + "must be within the limits of %.1f km. Current distance: %.2f km",
                                MAX_GOV_DISTANCE_KM, distance),
                        HttpStatus.CONFLICT);
            }
        } else if (org.getType().equals(OrganizationType.TRUST)) {
            if (distance > MAX_TRUST_DISTANCE_KM) {
                throw new MyException(
                        String.format(
                                "For trust organizations, official and postal addresses "
                                        + "must be within the limits of %.1f km. Current distance: %.2f km",
                                MAX_TRUST_DISTANCE_KM, distance),
                        HttpStatus.CONFLICT);
            }
        }
    }

    private double calculateDistance(float lat1, double lon1, float lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                        * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLon / 2)
                        * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }

    private void validateLatLonConstraints(float lat1, double lon1, float lat2, double lon2) {
        if (MIN_LATITUDE > lat1 || MIN_LATITUDE > lat2 || MAX_LATITUDE < lat1 || MAX_LATITUDE < lat2) {
            throw new MyException("The latitude should be in the interval from -90 to 90", HttpStatus.CONFLICT);
        }
        if (MIN_LONGITUDE > lon1 || MIN_LONGITUDE > lon2 || MAX_LONGITUDE < lon1 || MAX_LONGITUDE < lon2) {
            throw new MyException("The longitude must be in the interval from -180 to 180", HttpStatus.CONFLICT);
        }
    }
}
