import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        AuthService,
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([])
      ]
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('uses the default mock login when the API is unavailable', (done) => {
    service.login('admin', 'admin').subscribe({
      next: response => {
        expect(response.sucesso).toBeTrue();
        expect(response.dados.token).toBe('mock-token-github-pages');
        done();
      },
      error: done.fail
    });

    const request = httpMock.expectOne(request => request.url.endsWith('/auth/login'));
    request.error(new ProgressEvent('error'), { status: 0, statusText: 'Unknown Error' });
  });

  it('does not mock invalid credentials when the API is unavailable', (done) => {
    service.login('admin', 'senha-errada').subscribe({
      next: () => done.fail('Expected login to fail'),
      error: error => {
        expect(error.status).toBe(0);
        done();
      }
    });

    const request = httpMock.expectOne(request => request.url.endsWith('/auth/login'));
    request.error(new ProgressEvent('error'), { status: 0, statusText: 'Unknown Error' });
  });

  it('does not mock authentication errors from the API', (done) => {
    service.login('admin', 'admin').subscribe({
      next: () => done.fail('Expected login to fail'),
      error: error => {
        expect(error.status).toBe(401);
        done();
      }
    });

    const request = httpMock.expectOne(request => request.url.endsWith('/auth/login'));
    request.flush({ message: 'Usuario ou senha invalidos.' }, { status: 401, statusText: 'Unauthorized' });
  });
});
