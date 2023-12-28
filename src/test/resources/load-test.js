import http from "k6/http";
import { check } from 'k6';

export const options = {
  scenarios: {
      contacts: {
        executor: 'constant-vus',
        vus: 50,
        duration: '10s',
        gracefulStop: '0s',
      },
    },
};

export default function() {
  const res = http.get('http://localhost:8080' );

    check(res, {
      'is status 200': (r) => r.status === 200,
    });
};