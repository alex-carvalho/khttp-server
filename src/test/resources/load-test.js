import http from "k6/http";
import { check } from 'k6';

export const options = {
  scenarios: {
    base: {
      executor: 'constant-vus',
      vus: 50,
      duration: '10s',
      gracefulStop: '0s',
    },
  },
};

export default function() {
  const res = http.get(`http://localhost:${__ENV.PORT}`);

  check(res, {
    'is status 200': (r) => r.status === 200,
  });
};