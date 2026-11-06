package com.revenera.gcs.webservices;

import com.flexnet.external.type.*;
import com.flexnet.external.webservice.idgenerator.IdGeneratorException;
import com.flexnet.external.webservice.idgenerator.IdGeneratorServiceInterface;
import com.revenera.gcs.ApplicationProperties;
import com.revenera.gcs.Beans;
import com.revenera.gcs.ExecutionContext;
import com.revenera.gcs.ProficyConfiguration;
import com.revenera.gcs.logging.Level;
import com.revenera.gcs.utils.HandyBag;
import com.revenera.gcs.utils.Serializer;
import org.apache.commons.lang3.SystemProperties;
import org.apache.commons.lang3.SystemUtils;

import javax.jws.WebService;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;


@WebService(
    serviceName = "revenera-gcs",
    name = "Id Generator Service",
    endpointInterface = "com.flexnet.external.webservice.idgenerator.IdGeneratorServiceInterface",
    wsdlLocation = "WEB-INF/wsdl/schema/IdGeneratorService.wsdl")
public class IdGeneratorServiceImpl extends ServiceProperties implements IdGeneratorServiceInterface {

  private static Id makeLicensingId(final String value) {
    return new Id() {
      {
        this.id = value;
      }
    };
  }

  /** requires execution context */
  private final Function<IStringGenerator.Type, Id> create_licensing_identifier = type ->
      makeLicensingId(ExecutionContext.getStringGenerator().generateStandard(type));

  public IdGeneratorServiceImpl() {
    logger.me(this);
  }

  @Override
  public PingResponse ping(final PingRequest request) throws IdGeneratorException {
    logger.in();

    try (final ExecutionContext ctx = new ExecutionContext()) {
      ///register the request
      ExecutionContext.injectRequest(request);

      /// register the response
      return ExecutionContext.injectResponse(new PingResponse() {
        {
          final ApplicationProperties props = ExecutionContext.getApplicationProperties();
          this.str = Serializer.safeSerializeYaml(new HandyBag()
              .withJoined("version",
                  props.getVersion(),
                  props.getReleaseDate(),
                  props.getReleaseTime())
              .withJoined("proficy",
                  props.getItem(ApplicationProperties.Items.PROFICY_CHARSET),
                  props.getItem(ApplicationProperties.Items.PROFICY_SEPARATOR),
                  props.getItem(ApplicationProperties.Items.PROFICY_GROUP_COUNT),
                  props.getItem(ApplicationProperties.Items.PROFICY_GROUP_LENGTH),
                  props.getItem(ApplicationProperties.Items.PROFICY_PRODUCT_LINES),
                  props.getHousekeepingFrequency(),
                  props.getHousekeepingRetention())
              .withJoined("system",
                  SystemProperties.getOsName(),
                  SystemProperties.getOsVersion(),
                  SystemProperties.getOsArch()
              )
              .withJoined("host",
                  SystemUtils.getHostName(),
                  SystemProperties.getUserName("unknown"))
              .with("path", Beans.getResourcePath())
              .with("up-time", ExecutionContext.getApplicationDuration().toString()));

          this.info = Serializer.safeSerializeYaml(ExecutionContext.getApplicationData());

          this.processedTime = Instant.now().toString();
        }
      });
    }
    catch (final Throwable t) {
      logger.exception(t);
      return ServiceHelper.raiseIdGeneratorException(t);
    }
  }

  @Override
  public Id generateEntitlementID(final Entitlement request) throws IdGeneratorException {
    logger.in();
    try (final ExecutionContext ctx = new ExecutionContext()) {
      ///register the request
      ExecutionContext.injectRequest(request);

      return ExecutionContext.injectResponse(
          create_licensing_identifier.apply(IStringGenerator.Type.Entitlement));
    }
    catch (final Throwable t) {
      logger.exception(t);
      return ServiceHelper.raiseIdGeneratorException(t);
    }
    finally {
      logger.out();
    }
  }

  @Override
  public Id generateLineItemID(final EntitlementLineItem request) throws IdGeneratorException {
    logger.in();
    try (final ExecutionContext ctx = new ExecutionContext()) {
      final IStringGenerator generator = ExecutionContext.getStringGenerator();

      ///register the payload
      ExecutionContext.injectRequest(request);
      final ProficyConfiguration proficyConfiguration = ExecutionContext
          .getApplicationProperties()
          .getProficyConfiguration();
      logger.json(Level.DEBUG, proficyConfiguration);

      final List<String> lines =  request.getEntitledProducts()
              .stream()
              .map(x -> x.getProductCategory().getValue().getName())
              .collect(Collectors.toList());
      logger.json(Level.DEBUG, lines);

      /// if there is no product line in common (disjoint!)
      if (Collections.disjoint(proficyConfiguration.getProductLines(),lines)) {
        return ExecutionContext.injectResponse(
            create_licensing_identifier.apply(IStringGenerator.Type.Activation));
      }
      else {
        /// PROFICY
        return ExecutionContext.injectResponse(makeLicensingId(
            generator.generateGroups(
              proficyConfiguration.getCharset(),
              proficyConfiguration.getSeparator(),
              proficyConfiguration.getGroupCount(),
              proficyConfiguration.getGroupLength())));
      }
    }
    catch (final Throwable t) {
      logger.exception(t);
      return ServiceHelper.raiseIdGeneratorException(t);
    }
    finally {
      logger.out();
    }
  }

  @Override
  public Id generateWebRegKey(final BulkEntitlement request) throws IdGeneratorException {
    logger.in();
    try (final ExecutionContext ctx = new ExecutionContext()) {
      ///register the payload
      ExecutionContext.injectRequest(request);

      return ExecutionContext.injectResponse(
          create_licensing_identifier.apply(IStringGenerator.Type.Web));
    }
    catch (final Throwable t) {
      logger.exception(t);
      return ServiceHelper.raiseIdGeneratorException(t);
    }
    finally {
      logger.out();
    }
  }

  @Override
  public Id generateMaintenanceItemID(final MaintenanceItem request) throws IdGeneratorException {
    logger.in();
    try (final ExecutionContext ctx = new ExecutionContext()) {
      ///register the payload
      ExecutionContext.injectRequest(request);

      return ExecutionContext.injectResponse(
          create_licensing_identifier.apply(IStringGenerator.Type.Maintenance));
    }
    catch (final Throwable t) {
      logger.exception(t);
      return ServiceHelper.raiseIdGeneratorException(t);
    }
    finally {
      logger.out();
    }
  }

  @Override
  public Id generateFulfillmentID(final FulfillmentRecord request) throws IdGeneratorException {
    logger.in();
    try (final ExecutionContext ctx = new ExecutionContext()) {
      ///register the payload
      ExecutionContext.injectRequest(request);

      return ExecutionContext.injectResponse(
          create_licensing_identifier.apply(IStringGenerator.Type.Fulfilment));
    }
    catch (final Throwable t) {
      logger.exception(t);
      return ServiceHelper.raiseIdGeneratorException(t);
    }
    finally {
      logger.out();
    }
  }

  @Override
  public Id generateConsolidatedLicenseID(final ConsolidatedLicenseRecord request) throws IdGeneratorException {
    logger.in();
    try (final ExecutionContext ctx = new ExecutionContext()) {
      ///register the payload
      ExecutionContext.injectRequest(request);

      return ExecutionContext.injectResponse(
          create_licensing_identifier.apply(IStringGenerator.Type.Consolidated));
    }
    catch (final Throwable t) {
      logger.exception(t);
      return ServiceHelper.raiseIdGeneratorException(t);
    }
    finally {
      logger.out();
    }
  }
}
