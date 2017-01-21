package controllers;

import models.Category;
import play.Logger;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;

/**
 * Created by aphax on 03/11/16.
 */
public class REST extends Controller {
  @Inject
  FormFactory formFactory;

  public Result createCategory() {
    Form<Category> categoryForm = formFactory.form(Category.class).bindFromRequest();
    if (categoryForm.hasErrors()) {
      return badRequest();
    }
    Category category = categoryForm.get();
    Logger.debug("request data", request().body().asFormUrlEncoded().toString());
    Logger.debug("form data", categoryForm.data());
    Logger.debug("category", category.toString());
    return ok();
  }
}
