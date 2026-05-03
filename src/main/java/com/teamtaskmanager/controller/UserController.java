package com.teamtaskmanager.controller;

import com.teamtaskmanager.model.Task;
import com.teamtaskmanager.model.TaskStatus;
import com.teamtaskmanager.model.User;
import com.teamtaskmanager.service.ProjectService;
import com.teamtaskmanager.service.TaskService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private ProjectService projectService;

    // USER DASHBOARD

    @GetMapping("/user_dashboard")
    public String userDashboard(HttpSession session,
                                Model model) {

        try {

            User user =
                    (User) session.getAttribute("loggedInUser");

            if (user == null) {

                return "redirect:/login";
            }

            model.addAttribute("user", user);

            model.addAttribute(
                    "tasks",
                    taskService.getTasksByUser(user.getId())
            );

            model.addAttribute(
                    "projects",
                    projectService.getAllProjects()
            );

            return "user/user_dashboard";

        } catch (Exception e){

            System.out.println(
                    "User Dashboard Error : "
                            + e.getMessage());

            return "redirect:/login";
        }
    }

    // LOGOUT

    @GetMapping("/logout")
    public String logout(HttpSession session,
                         RedirectAttributes redirectAttributes) {

        try {

            session.invalidate();

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Logout Successfully!"
            );

        } catch (Exception e){

            System.out.println(
                    "Logout Error : "
                            + e.getMessage());
        }

        return "redirect:/login";
    }

    // UPDATE TASK STATUS

    @GetMapping("/task/update/{id}")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam TaskStatus status) {

        try {

            Task task = taskService.getTaskById(id);

            // STATUS UPDATE

            task.setStatus(status);

            // COMPLETED TIME

            if (status == TaskStatus.DONE) {

                task.setCompletedAt(
                        LocalDateTime.now());

            } else {

                task.setCompletedAt(null);
            }

            // SAVE TASK

            taskService.saveTask(task);

        } catch (Exception e){

            System.out.println(
                    "Update Task Status Error : "
                            + e.getMessage());
        }

        return "redirect:/user/user_dashboard";
    }

    // VIEW TASK DETAILS

    @GetMapping("/task/view/{id}")
    public String viewTask(@PathVariable Long id,
                           Model model) {

        try {

            Task task =
                    taskService.getTaskById(id);

            model.addAttribute("task", task);

            return "user/task_view";

        } catch (Exception e){

            System.out.println(
                    "View Task Error : "
                            + e.getMessage());

            return "redirect:/user/user_dashboard";
        }
    }
}