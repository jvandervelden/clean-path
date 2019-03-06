import { Injectable } from '@angular/core';

import { Observable, Subscriber } from 'rxjs';

import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';

@Injectable({
  providedIn: 'root'
})
export class TelematryService {

  constructor() { }

  private socket = null;
  private socketReady = false;
  private messageSubscriber: Subscriber<any>;
  private messageObserver: Observable<any> = new Observable<any>(observer => this.messageSubscriber = observer);

  public initSocket(): void {
    if (this.socket != null) {
      this.close();
    }

    let urlBits = window.location.href.split("/");

    this.socket = new WebSocket("ws://" + urlBits[2] + "/telematry/cleaner/0");
    this.socket.onopen = this.onOpen.bind(this);
    this.socket.onerror = this.onError.bind(this);
    this.socket.onclose = this.onClose.bind(this);
    this.socket.onmessage = this.onMessage.bind(this);
  }

  public onOpen(): void {
    this.socketReady = true;
  }

  public onError(): void {
    console.error("Error connecting to telematry socket");
  }

  public onClose(): void {
    this.socketReady = false;
  }

  public start(): Observable<any> {
    if (this.socket == null) {
      this.initSocket();
    }

    return this.messageObserver;
  }

  public stop(): void {
    this.close();
  }

  public onMessage(event): void {
    this.messageSubscriber.next(JSON.parse(event.data));
  }

  public close(): void {
    if (this.socket != null) {
      this.socket.close();
      this.socket = null;
      this.socketReady = false;
    }
  }
}
