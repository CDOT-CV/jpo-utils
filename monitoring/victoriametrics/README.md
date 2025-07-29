# VictoriaMetrics Configuration

This directory contains the configuration for VictoriaMetrics, which acts as a metrics aggregator and forwarder for the monitoring stack.

## Overview

VictoriaMetrics is configured to:
1. Accept remote write from Prometheus
2. Filter and forward specific Kafka metrics (`kafka_produced_rsu_messages_total`)
3. Provide remote read capabilities
4. Store metrics with configurable retention

## Configuration Files

### `scrape.yml`
Contains scrape configurations that mirror the Prometheus setup, allowing VictoriaMetrics to collect metrics directly from all exporters.

### `relabel.yml`
Contains relabeling rules that filter metrics to only forward `kafka_produced_rsu_messages_total` metrics.

## Environment Variables

The following environment variables can be configured in your `.env` file:

- `VICTORIAMETRICS_RETENTION`: Data retention period (default: 30d)
- `VICTORIAMETRICS_MAX_HOURLY_SERIES`: Maximum hourly series (default: 1000000)
- `VICTORIAMETRICS_MAX_UNIQUE_TIMESERIES`: Maximum unique time series (default: 1000000)
- `VICTORIAMETRICS_REMOTE_WRITE_URL`: URL to forward filtered metrics to (optional)
- `VICTORIAMETRICS_REMOTE_READ_URL`: URL for remote read operations (optional)

## Usage

### Starting VictoriaMetrics
```bash
# Start with monitoring profile
docker-compose --profile monitoring_full up victoriametrics

# Start only VictoriaMetrics
docker-compose --profile victoriametrics up
```

### Accessing VictoriaMetrics
- Web UI: http://localhost:8428
- Metrics endpoint: http://localhost:8428/metrics
- Health check: http://localhost:8428/health

### Querying Metrics
VictoriaMetrics supports PromQL queries. You can query the forwarded metrics:
```promql
kafka_produced_rsu_messages_total
```

### API Endpoints
- Write metrics: `POST /api/v1/write`
- Read metrics: `GET /api/v1/query`
- Query range: `GET /api/v1/query_range`

## Integration with Prometheus

Prometheus is configured to send all metrics to VictoriaMetrics via remote write. VictoriaMetrics then filters and forwards only the specified Kafka metrics to the configured remote write URL.

## Monitoring

VictoriaMetrics exposes its own metrics at `/metrics` endpoint, which can be scraped by Prometheus for monitoring the VictoriaMetrics instance itself. 