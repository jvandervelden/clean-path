import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { MapScreenComponent } from './components/map-screen/map-screen.component';

const routes: Routes = [{
  path: "map",
  component: MapScreenComponent
}, {
  path: "",
  component: MapScreenComponent
}];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
