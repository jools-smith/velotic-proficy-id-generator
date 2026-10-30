package com.revenera.gcs.webservices;

import com.flexnet.external.type.*;
import com.flexnet.external.webservice.idgenerator.IdGeneratorException;
import com.flexnet.external.webservice.idgenerator.IdGeneratorServiceInterface;
import com.revenera.gcs.Beans;
import com.revenera.gcs.ExecutionContext;
import com.revenera.gcs.ProficyConfiguration;
import com.revenera.gcs.logging.Level;
import com.revenera.gcs.utils.HandyBag;
import com.revenera.gcs.utils.Serializer;
import com.sun.media.sound.WaveExtensibleFileReader;
import jdk.jfr.internal.LogLevel;
import org.apache.commons.lang3.SystemProperties;
import org.apache.commons.lang3.SystemUtils;

import javax.jws.WebService;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@WebService(
    serviceName = "revenera-gcs",
    name = "Id Generator Service",
    endpointInterface = "com.flexnet.external.webservice.idgenerator.IdGeneratorServiceInterface",
    wsdlLocation = "WEB-INF/wsdl/schema/IdGeneratorService.wsdl")
public class IdGeneratorServiceImpl extends ServiceProperties implements IdGeneratorServiceInterface {

  /** static stuff... */
  private static String makeUuidBasedId(final String prefix) {
    return prefix + UUID.randomUUID()
        .toString()
        .replace("-", "_");
  }

  private static String makeFulfilmentId() {
    return makeUuidBasedId("FID_");
  }

  private static String makeConsolidatedId() {
    return makeUuidBasedId("CID_");
  }

  private static String makeStandardId() {
    return String.join("-",
        UUID.randomUUID()
            .toString()
            .replace("-", "")
            .split("(?<=\\G.{4})"));
  }

  private static Id makeId(final String value) {
    return new Id() {
      {
        this.id = value;
      }
    };
  }

  /** IdGeneratorServiceImpl */

  public IdGeneratorServiceImpl() {
    logger.me(this);
  }

  @Override
  public PingResponse ping(final PingRequest payload) throws IdGeneratorException {
    logger.in();

    try (final ExecutionContext ctx = new ExecutionContext()) {
      ///register the payload
      ExecutionContext.injectRequest(payload);

      //TODO:debuggery...
      ExecutionContext.injectPayload("hello");
      ExecutionContext.injectPayload("world");

      /// register the response
      return ExecutionContext.injectResponse(new PingResponse() {
        {
          this.str = Serializer.safeSerializeYaml(new HandyBag()
              .withJoined("version",
                  ExecutionContext.getApplicationProperties().getVersion(),
                  ExecutionContext.getApplicationProperties().getReleaseDate(),
                  ExecutionContext.getApplicationProperties().getReleaseTime())
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
  public Id generateEntitlementID(final Entitlement payload) throws IdGeneratorException {
    logger.in();
    try (final ExecutionContext ctx = new ExecutionContext()) {
      ///register the payload
      ExecutionContext.injectRequest(payload);

      return ExecutionContext.injectResponse(makeId(makeStandardId()));
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
  public Id generateLineItemID(final EntitlementLineItem payload) throws IdGeneratorException {
    logger.in();
    try (final ExecutionContext ctx = new ExecutionContext()) {
      ///register the payload
      ExecutionContext.injectRequest(payload);
      final ProficyConfiguration proficyConfiguration = ExecutionContext
          .getApplicationProperties()
          .getProficyConfiguration();
      logger.json(Level.DEBUG, proficyConfiguration);

      final List<String> lines =  payload.getEntitledProducts()
              .stream()
              .map(x -> x.getProductCategory().getValue().getName())
              .collect(Collectors.toList());
      logger.json(Level.DEBUG, lines);

      // is there a product line in common
      final boolean isProficy = !Collections.disjoint(proficyConfiguration.getProductLines(),lines);

      return ExecutionContext.injectResponse(makeId(
          isProficy ?
          // this is proficy
          ExecutionContext.getStringGenerator().generate(
              proficyConfiguration.getCharset(),
              proficyConfiguration.getSeparator(),
              proficyConfiguration.getGroupCount(),
              proficyConfiguration.getGroupLength()) :
          // otherwise us the standard one
          makeStandardId()));
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
  public Id generateWebRegKey(final BulkEntitlement payload) throws IdGeneratorException {
    logger.in();
    try (final ExecutionContext ctx = new ExecutionContext()) {
      ///register the payload
      ExecutionContext.injectRequest(payload);

      return ExecutionContext.injectResponse(makeId(makeStandardId()));
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
  public Id generateMaintenanceItemID(final MaintenanceItem payload) throws IdGeneratorException {
    logger.in();
    try (final ExecutionContext ctx = new ExecutionContext()) {
      ///register the payload
      ExecutionContext.injectRequest(payload);

      return ExecutionContext.injectResponse(makeId(makeStandardId()));
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
  public Id generateFulfillmentID(final FulfillmentRecord payload) throws IdGeneratorException {
    logger.in();
    try (final ExecutionContext ctx = new ExecutionContext()) {
      ///register the payload
      ExecutionContext.injectRequest(payload);

      return ExecutionContext.injectResponse(makeId(makeFulfilmentId()));
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
  public Id generateConsolidatedLicenseID(final ConsolidatedLicenseRecord payload) throws IdGeneratorException {
    logger.in();
    try (final ExecutionContext ctx = new ExecutionContext()) {
      ///register the payload
      ExecutionContext.injectRequest(payload);

      return ExecutionContext.injectResponse(makeId(makeConsolidatedId()));
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
