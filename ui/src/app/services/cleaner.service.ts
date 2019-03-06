import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class CleanerService {

  constructor(private http: HttpClient) { }

  getCleaner() {
    return this.http.get("/api/cleaners/0");
  }

  startCleaning(currentCleaner: any) {
    return this.http.post("/api/cleaners/0/start", currentCleaner);
  }
}
